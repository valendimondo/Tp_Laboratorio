package modelo;

public abstract class Cliente {
    protected String nombre;
    protected double ingresos;
    protected double gastos;
    protected boolean tieneBuenHistorial;

    public Cliente(String nombre, double ingresos, double gastos, boolean tieneBuenHistorial) {
        this.nombre = nombre;
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.tieneBuenHistorial = tieneBuenHistorial;
    }

    // Cada tipo de cliente evalúa el riesgo a su manera
    public abstract double evaluarRiesgo();

    public String getNombre() { return nombre; }
    public double getIngresos() { return ingresos; }
    public double getGastos() { return gastos; }
    public boolean isTieneBuenHistorial() { return tieneBuenHistorial; }
}