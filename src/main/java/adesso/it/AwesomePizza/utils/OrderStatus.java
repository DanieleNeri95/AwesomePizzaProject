package adesso.it.AwesomePizza.utils;

public enum OrderStatus {
    QUEUED("IN CODA"),            // Ordine in attesa nella coda
    PICKED_UP("PRESO IN CARICO"), // Ordine preso in carico dal sistema
    DELIVERED("CONSEGNATO");      // Ordine consegnato al cliente

    private final String description;

    // Costruttore dell'enum
    OrderStatus(String description) {
        this.description = description;
    }

    // Metodo per ottenere la descrizione dello stato
    public String getDescription() {
        return description;
    }
}