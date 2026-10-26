public abstract class Cliente {
    protected final String nombre;
    protected final double ingresos;
    protected final double gastos;
    protected final boolean tieneBuenHistorial;

    public Cliente(String nombre, double ingresos, double gastos, boolean tieneBuenHistorial) {
        this.nombre = nombre;
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.tieneBuenHistorial = tieneBuenHistorial;
    }

    /** 0 = no apto, 0.5 = apto con reservas, 1 = apto. */
    public abstract double evaluarRiesgo();

    public String getNombre() { return nombre; }
    public double getIngresos() { return ingresos; }
    public double getGastos() { return gastos; }
    public boolean tieneBuenHistorial() { return tieneBuenHistorial; }
}
