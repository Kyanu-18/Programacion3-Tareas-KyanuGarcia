import javafx.collections.ObservableList;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
 
public class GestorArchivos {
 
    public static boolean existePlaca(ObservableList<Vehiculo> lista, String placa) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPlaca().equalsIgnoreCase(placa)) {
                return true;
            }
        }
        return false;
    }
 
    public static void exportarCSV(File archivo, ObservableList<Vehiculo> lista) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivo))) {
            escritor.println("placa,modelo,tipo,velocidadMax,precio");
            for (int i = 0; i < lista.size(); i++) {
                Vehiculo v = lista.get(i);
                escritor.println(v.getPlaca() + "," + v.getModelo() + "," + v.getTipo() + ","
                        + v.getVelocidadMax() + "," + v.getPrecio());
            }
        }
    }
 
    public static int[] importarCSV(File archivo, ObservableList<Vehiculo> lista) throws IOException {
        int cargados = 0;
        int descartados = 0;
 
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea = lector.readLine();
 
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split(",");
 
                if (partes.length != 5) {
                    descartados++;
                    continue;
                }
 
                try {
                    String placa = partes[0].trim();
                    String modelo = partes[1].trim();
                    String tipo = partes[2].trim();
                    int velocidad = Integer.parseInt(partes[3].trim());
                    int precio = Integer.parseInt(partes[4].trim());
 
                    if (placa.isEmpty() || modelo.isEmpty() || tipo.isEmpty() || velocidad <= 0 || precio < 0
                            || existePlaca(lista, placa)) {
                        descartados++;
                    } else {
                        lista.add(new Vehiculo(placa, modelo, tipo, velocidad, precio));
                        cargados++;
                    }
                } catch (NumberFormatException e) {
                    descartados++;
                }
            }
        }
 
        return new int[] { cargados, descartados };
    }
 
    public static void guardarPartida(ObservableList<Vehiculo> lista) throws IOException {
        ArrayList<Vehiculo> copia = new ArrayList<Vehiculo>(lista);
 
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("garaje.dat"))) {
            salida.writeObject(copia);
        }
    }
 
    public static ArrayList<Vehiculo> cargarPartida() throws IOException, ClassNotFoundException {
        File archivo = new File("garaje.dat");
 
        if (!archivo.exists()) {
            return new ArrayList<Vehiculo>();
        }
 
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ArrayList<Vehiculo>) entrada.readObject();
        }
    }
 
    public static void escribirBitacora(String accion, Vehiculo v) throws IOException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String fecha = LocalDateTime.now().format(formato);
 
        try (PrintWriter escritor = new PrintWriter(new FileWriter("bitacora.txt", true))) {
            escritor.println("[" + fecha + "] " + accion + " - " + v.getPlaca() + " - " + v.getModelo());
        }
    }
}