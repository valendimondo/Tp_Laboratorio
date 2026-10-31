package modelo;

public class PersonaFisica extends Cliente {

    public PersonaFisica(String nombre, double ingresos, double gastos, boolean tieneBuenHistorial) {
        super(nombre, ingresos, gastos, tieneBuenHistorial);
    }

    @Override
    public double evaluarRiesgo() {
        if (!tieneBuenHistorial) return 0;               // sin buen historial: no apto
        double margen = ingresos - gastos;
        return margen >= ingresos * 0.30 ? 1 : 0.5;      // umbral: 30% de los ingresos
    }
}