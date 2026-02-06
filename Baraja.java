public class Baraja {
    String[] figuras = {"Picas", "Corazones", "Diamantes", "Treboles"};
    String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "As"};

    public String sacarCarta() {
        int figura = (int) (Math.random() * 4); 
        int valor = (int) (Math.random() * 13); 
        return valores[valor] + " de " + figuras[figura];
    }

    public int valorDe(String carta) {
        String cart = carta.split(" ")[0];
        if (cart.equals("As")) return 11;
        if (cart.equals("J") || cart.equals("Q") || cart.equals("K")) return 10;
        return Integer.parseInt(cart);
    }
}