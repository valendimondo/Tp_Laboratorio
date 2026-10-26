public class PersonaFisica extends Cliente {
    public PersonaFisica(String nombre, double ingresos, double gastos, boolean historial) {
        super(nombre, ingresos, gastos, historial);
    }

    @Override
    public double evaluarRiesgo() {
        if (!tieneBuenHistorial) return 0;
        double margen = ingresos - gastos;
        return margen >= ingresos * 0.30 ? 1 : 0.5;
    }
}
