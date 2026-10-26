import javax.swing.*;
import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Ejecuta trabajo de BD fuera del hilo de Swing. Garantiza que, pase lo que pase,
 * la barra de progreso se detiene y los controles se rehabilitan (interfaz consistente).
 */
public class Tarea {
    public static <T> void ejecutar(JProgressBar barra, Component[] bloquear,
                                    Callable<T> trabajo, Consumer<T> alExito,
                                    Consumer<Throwable> alError) {
        barra.setIndeterminate(true);
        for (Component c : bloquear) c.setEnabled(false);

        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return trabajo.call(); }

            @Override protected void done() {
                // 1) restaurar SIEMPRE la interfaz
                barra.setIndeterminate(false);
                for (Component c : bloquear) c.setEnabled(true);
                // 2) luego informar resultado
                try {
                    alExito.accept(get());
                } catch (ExecutionException e) {
                    alError.accept(e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
