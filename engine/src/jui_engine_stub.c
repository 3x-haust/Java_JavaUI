#include "../include/jui_engine.h"
#include <stdlib.h>
#include <stdio.h>
#include <sys/stat.h>
#include <string.h>
#include <stdint.h>

struct jui_engine {
    int width;
    int height;
    int device_type;
    unsigned int frame_id;
    // last patch
    uint8_t* patch;
    uint32_t patch_size;
    // nodes indexed by id (1..N) for simplicity
    struct node** nodes;
    int nodes_cap;
    int max_id;
};

// ----- IR & Scene -----
enum {
    NODE_COLUMN = 1,
    NODE_ROW = 2,
    NODE_CENTER = 3,
    NODE_SIZED_BOX = 4,
    NODE_TEXT = 5,
    NODE_BUTTON = 6
};

enum {
    PROP_MAIN_ALIGN = 101,
    PROP_CROSS_ALIGN = 102,
    PROP_GAP = 103,
    PROP_PADDING_LTRB = 104,
    PROP_TEXT = 201,
    PROP_SIZE_W = 301,
    PROP_SIZE_H = 302
};

typedef struct node {
    int id;
    int type;
    int mainAlign;
    int crossAlign;
    int gap;
    int paddingLTRB;
    int width;
    int height;
    char* text;
    struct node** children;
    int childCount;
    int childCap;
    struct node* parent;
    // layout result
    int x, y, w, h;
} node_t;

static void ensure_nodes_cap(jui_engine_t* e, int cap) {
    if (cap <= e->nodes_cap) return;
    int newCap = e->nodes_cap ? e->nodes_cap : 64;
    while (newCap < cap) newCap *= 2;
    e->nodes = (node_t**)realloc(e->nodes, (size_t)newCap * sizeof(node_t*));
    for (int i = e->nodes_cap; i < newCap; ++i) e->nodes[i] = NULL;
    e->nodes_cap = newCap;
}

static node_t* get_node(jui_engine_t* e, int id) {
    if (id <= 0) return NULL;
    if (id >= e->nodes_cap) return NULL;
    return e->nodes[id];
}

static node_t* create_node(jui_engine_t* e, int id, int type) {
    if (id <= 0) return NULL;
    if (id >= e->nodes_cap) ensure_nodes_cap(e, id + 1);
    node_t* n = e->nodes[id];
    if (!n) {
        n = (node_t*)calloc(1, sizeof(node_t));
        n->id = id;
        n->type = type;
        n->width = 0; n->height = 0;
        n->mainAlign = 0; n->crossAlign = 0; n->gap = 0; n->paddingLTRB = 0;
        e->nodes[id] = n;
        if (id > e->max_id) e->max_id = id;
    } else {
        n->type = type;
    }
    return n;
}

static void append_child(node_t* parent, node_t* child) {
    if (!parent || !child) return;
    if (child->parent && child->parent != parent) {
        // detach from previous (naive)
        node_t* p = child->parent;
        for (int i = 0; i < p->childCount; ++i) {
            if (p->children[i] == child) {
                memmove(&p->children[i], &p->children[i+1], (size_t)(p->childCount - i - 1) * sizeof(node_t*));
                p->childCount--;
                break;
            }
        }
        child->parent = NULL;
    }
    if (parent->childCount + 1 > parent->childCap) {
        int nc = parent->childCap ? parent->childCap * 2 : 4;
        parent->children = (node_t**)realloc(parent->children, (size_t)nc * sizeof(node_t*));
        parent->childCap = nc;
    }
    parent->children[parent->childCount++] = child;
    child->parent = parent;
}

static void free_node_tree(node_t* n) {
    if (!n) return;
    free(n->text);
    free(n->children);
    free(n);
}

jui_engine_t* jui_engine_create(const jui_engine_config_t* cfg) {
    jui_engine_t* e = (jui_engine_t*)calloc(1, sizeof(jui_engine_t));
    if (!e) return NULL;
    e->width = cfg ? cfg->width : 640;
    e->height = cfg ? cfg->height : 480;
    e->device_type = cfg ? cfg->device_type : 3;
    e->frame_id = 0;
    return e;
}

void jui_engine_destroy(jui_engine_t* eng) {
    if (!eng) return;
    if (eng->nodes) {
        for (int i = 1; i <= eng->max_id; ++i) {
            if (eng->nodes[i]) free_node_tree(eng->nodes[i]);
        }
        free(eng->nodes);
    }
    free(eng->patch);
    free(eng);
}

void jui_apply_patches(jui_engine_t* eng, const void* data, uint32_t size) {
    if (!eng || !data || size < 6) return;
    // keep raw bytes
    if (eng->patch) { free(eng->patch); eng->patch = NULL; eng->patch_size = 0; }
    eng->patch = (uint8_t*)malloc(size);
    if (eng->patch) { memcpy(eng->patch, data, size); eng->patch_size = size; }

    const uint8_t* p = (const uint8_t*)data;
    // header
    if (p[0] != 1) return; // version
    const uint8_t* cur = p + 6;
    const uint8_t* end = (const uint8_t*)data + size;
    while (cur < end) {
        uint8_t op = *cur++;
        switch (op) {
            case 1: { // CREATE_NODE
                if (cur + 8 > end) { cur = end; break; }
                int32_t id = *(int32_t*)cur; cur += 4;
                int32_t type = *(int32_t*)cur; cur += 4;
                create_node(eng, id, type);
                break; }
            case 2: { // DELETE_NODE (naive)
                if (cur + 4 > end) { cur = end; break; }
                int32_t id = *(int32_t*)cur; cur += 4;
                if (id > 0 && id <= eng->nodes_cap && eng->nodes[id]) {
                    free_node_tree(eng->nodes[id]);
                    eng->nodes[id] = NULL;
                }
                break; }
            case 3: { // SET_PROP (int/float union simplified as int)
                if (cur + 12 > end) { cur = end; break; }
                int32_t id = *(int32_t*)cur; cur += 4;
                int32_t key = *(int32_t*)cur; cur += 4;
                int32_t val;
                memcpy(&val, cur, 4); cur += 4;
                node_t* n = get_node(eng, id);
                if (n) {
                    switch (key) {
                        case PROP_MAIN_ALIGN: n->mainAlign = val; break;
                        case PROP_CROSS_ALIGN: n->crossAlign = val; break;
                        case PROP_GAP: n->gap = val; break;
                        case PROP_PADDING_LTRB: n->paddingLTRB = val; break;
                        case PROP_SIZE_W: n->width = val; break;
                        case PROP_SIZE_H: n->height = val; break;
                        default: break;
                    }
                }
                break; }
            case 4: { // APPEND_CHILD
                if (cur + 8 > end) { cur = end; break; }
                int32_t parent = *(int32_t*)cur; cur += 4;
                int32_t child = *(int32_t*)cur; cur += 4;
                node_t* pn = get_node(eng, parent);
                node_t* cn = get_node(eng, child);
                if (pn && cn) append_child(pn, cn);
                break; }
            case 5: { // INSERT_CHILD (ignored → append)
                if (cur + 12 > end) { cur = end; break; }
                int32_t parent = *(int32_t*)cur; cur += 4;
                int32_t child = *(int32_t*)cur; cur += 4;
                cur += 4; // index
                node_t* pn = get_node(eng, parent);
                node_t* cn = get_node(eng, child);
                if (pn && cn) append_child(pn, cn);
                break; }
            case 6: { // REMOVE_CHILD (naive)
                if (cur + 8 > end) { cur = end; break; }
                int32_t parent = *(int32_t*)cur; cur += 4;
                int32_t child = *(int32_t*)cur; cur += 4;
                node_t* pn = get_node(eng, parent);
                node_t* cn = get_node(eng, child);
                if (pn && cn) {
                    for (int i = 0; i < pn->childCount; ++i) if (pn->children[i] == cn) {
                        memmove(&pn->children[i], &pn->children[i+1], (size_t)(pn->childCount - i - 1) * sizeof(node_t*));
                        pn->childCount--; cn->parent = NULL; break;
                    }
                }
                break; }
            case 7: { // REPLACE_CHILD
                if (cur + 12 > end) { cur = end; break; }
                int32_t parent = *(int32_t*)cur; cur += 4;
                int32_t oldc = *(int32_t*)cur; cur += 4;
                int32_t newc = *(int32_t*)cur; cur += 4;
                node_t* pn = get_node(eng, parent);
                node_t* on = get_node(eng, oldc);
                node_t* nn = get_node(eng, newc);
                if (pn && on && nn) {
                    for (int i = 0; i < pn->childCount; ++i) if (pn->children[i] == on) { pn->children[i] = nn; nn->parent = pn; on->parent = NULL; break; }
                }
                break; }
            case 8: { // SET_PROP_STR
                if (cur + 12 > end) { cur = end; break; }
                int32_t id = *(int32_t*)cur; cur += 4;
                int32_t key = *(int32_t*)cur; cur += 4;
                int32_t slen = *(int32_t*)cur; cur += 4;
                const char* s = (const char*)cur;
                if (cur + slen > end) { cur = end; break; }
                node_t* n = get_node(eng, id);
                if (n && key == PROP_TEXT) {
                    free(n->text);
                    n->text = (char*)malloc((size_t)slen + 1);
                    if (n->text) { memcpy(n->text, s, (size_t)slen); n->text[slen] = '\0'; }
                }
                cur += slen;
                break; }
            default:
                cur = end; break;
        }
    }
}

static void ensure_out_dir(void) {
#ifdef _WIN32
    _mkdir("build-native");
    _mkdir("build-native/out");
#else
    mkdir("build-native", 0755);
    mkdir("build-native/out", 0755);
#endif
}

static void write_ppm(const char* path, const unsigned char* rgb, int w, int h) {
    FILE* f = fopen(path, "wb");
    if (!f) return;
    fprintf(f, "P6\n%d %d\n255\n", w, h);
    fwrite(rgb, 1, (size_t)(w*h*3), f);
    fclose(f);
}

static void layout_node(node_t* n, int x, int y, int w, int h) {
    n->x = x; n->y = y; n->w = w; n->h = h;
    int padL = (n->paddingLTRB) & 0xFF;
    int padT = (n->paddingLTRB >> 24) & 0xFF;
    int padR = (n->paddingLTRB >> 16) & 0xFF;
    int padB = (n->paddingLTRB >> 8) & 0xFF;
    int cx = x + padL, cy = y + padT;
    int cw = w - padL - padR, ch = h - padT - padB;
    if (cw < 0) cw = 0; if (ch < 0) ch = 0;

    switch (n->type) {
        case NODE_COLUMN: {
            int curY = cy;
            for (int i = 0; i < n->childCount; ++i) {
                node_t* c = n->children[i];
                int chh = c->height > 0 ? c->height : 32;
                int chw = c->width > 0 ? c->width : cw;
                int cxoff = cx;
                if (n->crossAlign == 1) cxoff = cx + (cw - chw)/2; // center
                else if (n->crossAlign == 2) cxoff = cx + (cw - chw); // end
                layout_node(c, cxoff, curY, chw, chh);
                curY += chh + n->gap;
            }
            break; }
        case NODE_ROW: {
            int curX = cx;
            for (int i = 0; i < n->childCount; ++i) {
                node_t* c = n->children[i];
                int chw = c->width > 0 ? c->width : 96;
                int chh = c->height > 0 ? c->height : ch;
                int cyoff = cy;
                if (n->crossAlign == 1) cyoff = cy + (ch - chh)/2; // center
                else if (n->crossAlign == 2) cyoff = cy + (ch - chh); // end
                layout_node(c, curX, cyoff, chw, chh);
                curX += chw + n->gap;
            }
            break; }
        case NODE_CENTER: {
            if (n->childCount > 0) {
                node_t* c = n->children[0];
                int chw = c->width > 0 ? c->width : cw/2;
                int chh = c->height > 0 ? c->height : ch/2;
                layout_node(c, cx + (cw - chw)/2, cy + (ch - chh)/2, chw, chh);
            }
            break; }
        case NODE_SIZED_BOX: {
            if (n->childCount > 0) {
                node_t* c = n->children[0];
                int chw = c->width > 0 ? c->width : n->w;
                int chh = c->height > 0 ? c->height : n->h;
                layout_node(c, n->x, n->y, chw, chh);
            }
            break; }
        default: break;
    }
}

static void draw_rect(unsigned char* buf, int w, int h, int x, int y, int rw, int rh, unsigned char r, unsigned char g, unsigned char b) {
    for (int yy = y; yy < y + rh; ++yy) {
        if (yy < 0 || yy >= h) continue;
        for (int xx = x; xx < x + rw; ++xx) {
            if (xx < 0 || xx >= w) continue;
            size_t idx = (size_t)(yy*w + xx) * 3u;
            buf[idx+0] = r; buf[idx+1] = g; buf[idx+2] = b;
        }
    }
}

static node_t* find_root(jui_engine_t* e) {
    for (int i = 1; i <= e->max_id; ++i) {
        node_t* n = e->nodes[i];
        if (n && n->parent == NULL) return n;
    }
    return NULL;
}

void jui_begin_frame(jui_engine_t* eng, double time_nanos) {
    (void)time_nanos;
    if (!eng) return;
    int w = eng->width, h = eng->height;
    size_t sz = (size_t)w * (size_t)h * 3u;
    unsigned char* buf = (unsigned char*)malloc(sz);
    if (!buf) return;
    // clear to dark gray
    for (int y = 0; y < h; ++y) {
        for (int x = 0; x < w; ++x) {
            size_t idx = (size_t)(y*w + x) * 3u;
            buf[idx+0] = 24; buf[idx+1] = 24; buf[idx+2] = 24;
        }
    }
    // If we have a parsed scene, layout and draw recursively
    node_t* root = find_root(eng);
    if (root) {
        layout_node(root, 0, 0, w, h);
        // draw nodes (very naive, leaves and container frames)
        for (int i = 1; i <= eng->max_id; ++i) {
            node_t* n = eng->nodes[i];
            if (!n) continue;
            if (n->type == NODE_TEXT || n->type == NODE_BUTTON || n->childCount == 0) {
                draw_rect(buf, w, h, n->x+4, n->y+4, n->w-8, n->h-8, 66, 135, 245);
            } else {
                // container: outline
                // top
                draw_rect(buf, w, h, n->x, n->y, n->w, 2, 160,160,160);
                // bottom
                draw_rect(buf, w, h, n->x, n->y + n->h - 2, n->w, 2, 160,160,160);
                // left
                draw_rect(buf, w, h, n->x, n->y, 2, n->h, 160,160,160);
                // right
                draw_rect(buf, w, h, n->x + n->w - 2, n->y, 2, n->h, 160,160,160);
            }
        }
    }
    ensure_out_dir();
    char path[256];
    snprintf(path, sizeof(path), "build-native/out/frame_%u.ppm", eng->frame_id++);
    write_ppm(path, buf, w, h);
    free(buf);
}

void jui_pointer_event(jui_engine_t* eng, int action, float x, float y, int buttons) {
    (void)eng; (void)action; (void)x; (void)y; (void)buttons;
}

void jui_key_event(jui_engine_t* eng, int keycode, int down, int mods) {
    (void)eng; (void)keycode; (void)down; (void)mods;
}


