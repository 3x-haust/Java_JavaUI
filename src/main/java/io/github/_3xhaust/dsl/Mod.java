package io.github._3xhaust.dsl;

import java.awt.*;

public class Mod {

    public static FontModifier size(int fontSize) {
        return new FontModifier().size(fontSize);
    }

    public static FontModifier family(String fontFamily) {
        return new FontModifier().family(fontFamily);
    }

    public static FontModifier weightBold() {
        return new FontModifier().weight(Font.BOLD);
    }

    public static FontModifier weightItalic() {
        return new FontModifier().weight(Font.ITALIC);
    }

    public static FontModifier weightNormal() {
        return new FontModifier().weight(Font.PLAIN);
    }

    public static class FontModifier {
        private String family;
        private int size = -1;
        private int weight = -1;

        public FontModifier family(String family) {
            this.family = family;
            return this;
        }

        public FontModifier size(int size) {
            this.size = size;
            return this;
        }

        public FontModifier weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Font toFont() {
            String fontFamily = family != null ? family : Font.SANS_SERIF;
            int fontSize = size != -1 ? size : 12;
            int fontWeight = weight != -1 ? weight : Font.PLAIN;
            return new Font(fontFamily, fontWeight, fontSize);
        }

        public String getFamily() { return family; }
        public int getSize() { return size; }
        public int getWeight() { return weight; }
    }
}
