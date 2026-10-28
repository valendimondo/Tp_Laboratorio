package modelo;

public class PersonaFisica extends Cliente {

    public PersonaFisica(String nombre, double ingresos, double gastos, boolean tieneBuenHistorial) {
        super(nombre, ingresos, gastos, tieneBuenHistorial);
    }

    @Override
    public double evaluarRiesgo() {
        if (!tieneBuenHistorial) {
            return 0; // sin buen historial: riesgo nulo (no apto)
        }
        double margen = ingresos - gastos;
        double umbral = ingresos * 0.30; // 30% de los ingresos
        return margen > umbral ? 1 : 0;
    }
}