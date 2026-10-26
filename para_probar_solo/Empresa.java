public class Empresa extends Cliente {
    private final String rubro;
    private final String director;

    public Empresa(String nombre, double ingresos, double gastos, boolean historial,
                   String rubro, String director) {
        super(nombre, ingresos, gastos, historial);
        this.rubro = rubro;
        this.director = director;
    }

    @Override
    public double evaluarRiesgo() {
        if (gastos == 0) return 1;              // evita división por cero
        double ratio = ingresos / gastos;
        return ratio > 1.5 ? 1 : 0.5;
    }

    public String getRubro() { return rubro; }
    public String getDirector() { return director; }
}
