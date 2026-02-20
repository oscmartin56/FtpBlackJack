public class Baraja {
    String[] figuras = {"Picas", "Corazones", "Diamantes", "Treboles"};
    String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "As"};

    public String sacarCarta() {
        int f = (int) (Math.random() * 4); 
        int v = (int) (Math.random() * 13); 
        return valores[v] + " de " + figuras[f];
    }

    public int valorDe(String carta) {
        String v = carta.split(" ")[0];
        if (v.equals("As")) return 11;
        if (v.equals("J") || v.equals("Q") || v.equals("K")) return 10;
        return Integer.parseInt(v);
    }
}