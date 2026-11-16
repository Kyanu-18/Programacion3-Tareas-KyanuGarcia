import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
import java.io.File;
import java.io.IOException;
 
public class App extends Application {
 
    ObservableList<Vehiculo> lista = FXCollections.observableArrayList();
    TableView<Vehiculo> tabla = new TableView<Vehiculo>();
 
    TextField campoPlaca = new TextField();
    TextField campoModelo = new TextField();
    ComboBox<String> comboTipo = new ComboBox<String>();
    TextField campoVelocidad = new TextField();
    TextField campoPrecio = new TextField();
 
    Stage ventana;
 
    @Override
    public void start(Stage stage) {
        ventana = stage;
 
        try {
            lista.addAll(GestorArchivos.cargarPartida());
        } catch (IOException | ClassNotFoundException e) {
            mostrarError("No se pudo cargar garaje.dat: " + e.getMessage());
        }
 
        comboTipo.getItems().addAll("Deportivo", "Moto", "SUV", "Lancha", "Avioneta");
        comboTipo.setPromptText("Selecciona el tipo");
 
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.addRow(0, new Label("Placa:"), campoPlaca);
        formulario.addRow(1, new Label("Modelo:"), campoModelo);
        formulario.addRow(2, new Label("Tipo:"), comboTipo);
        formulario.addRow(3, new Label("Velocidad maxima (km/h):"), campoVelocidad);
        formulario.addRow(4, new Label("Precio ($):"), campoPrecio);
 
        TableColumn<Vehiculo, String> colPlaca = new TableColumn<Vehiculo, String>("Placa");
        colPlaca.setCellValueFactory(new PropertyValueFactory<Vehiculo, String>("placa"));
 
        TableColumn<Vehiculo, String> colModelo = new TableColumn<Vehiculo, String>("Modelo");
        colModelo.setCellValueFactory(new PropertyValueFactory<Vehiculo, String>("modelo"));
 
        TableColumn<Vehiculo, String> colTipo = new TableColumn<Vehiculo, String>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<Vehiculo, String>("tipo"));
 
        TableColumn<Vehiculo, Integer> colVelocidad = new TableColumn<Vehiculo, Integer>("Velocidad max");
        colVelocidad.setCellValueFactory(new PropertyValueFactory<Vehiculo, Integer>("velocidadMax"));
 
        TableColumn<Vehiculo, Integer> colPrecio = new TableColumn<Vehiculo, Integer>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<Vehiculo, Integer>("precio"));
 
        tabla.getColumns().addAll(colPlaca, colModelo, colTipo, colVelocidad, colPrecio);
        tabla.setItems(lista);
 
        Button botonAgregar = new Button("Agregar");
        botonAgregar.setOnAction(e -> agregar());
 
        Button botonEliminar = new Button("Eliminar seleccionado");
        botonEliminar.setOnAction(e -> eliminar());
 
        Button botonLimpiar = new Button("Limpiar formulario");
        botonLimpiar.setOnAction(e -> limpiar());
 
        Button botonExportar = new Button("Exportar CSV");
        botonExportar.setOnAction(e -> exportar());
 
        Button botonImportar = new Button("Importar CSV");
        botonImportar.setOnAction(e -> importar());
 
        Button botonGuardar = new Button("Guardar partida");
        botonGuardar.setOnAction(e -> guardar());
 
        HBox botones = new HBox(10);
        botones.getChildren().addAll(botonAgregar, botonEliminar, botonLimpiar, botonExportar, botonImportar,
                botonGuardar);
 
        VBox contenedor = new VBox(15);
        contenedor.setPadding(new Insets(15));
        contenedor.getChildren().addAll(formulario, botones, tabla);
 
        Scene escena = new Scene(contenedor, 800, 550);
        stage.setTitle("Garaje de Vice City");
        stage.setScene(escena);
        stage.show();
    }
 
    public void agregar() {
        String placa = campoPlaca.getText().trim();
        String modelo = campoModelo.getText().trim();
        String tipo = comboTipo.getValue();
        String textoVelocidad = campoVelocidad.getText().trim();
        String textoPrecio = campoPrecio.getText().trim();
 
        if (placa.isEmpty() || modelo.isEmpty() || tipo == null || textoVelocidad.isEmpty()
                || textoPrecio.isEmpty()) {
            mostrarError("Todos los campos son obligatorios");
            return;
        }
 
        if (placa.contains(",") || modelo.contains(",")) {
            mostrarError("La placa y el modelo no pueden tener comas");
            return;
        }
 
        int velocidad;
        int precio;
 
        try {
            velocidad = Integer.parseInt(textoVelocidad);
            precio = Integer.parseInt(textoPrecio);
        } catch (NumberFormatException e) {
            mostrarError("La velocidad y el precio deben ser numeros enteros");
            return;
        }
 
        if (velocidad <= 0) {
            mostrarError("La velocidad debe ser mayor que 0");
            return;
        }
 
        if (precio < 0) {
            mostrarError("El precio no puede ser negativo");
            return;
        }
 
        if (GestorArchivos.existePlaca(lista, placa)) {
            mostrarError("Ya existe un vehiculo con esa placa");
            return;
        }
 
        Vehiculo v = new Vehiculo(placa, modelo, tipo, velocidad, precio);
        lista.add(v);
 
        try {
            GestorArchivos.escribirBitacora("AGREGAR", v);
        } catch (IOException e) {
            mostrarError("No se pudo escribir en bitacora.txt: " + e.getMessage());
        }
 
        limpiar();
    }
 
    public void eliminar() {
        Vehiculo seleccionado = tabla.getSelectionModel().getSelectedItem();
 
        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecciona un vehiculo de la tabla");
            alerta.showAndWait();
            return;
        }
 
        lista.remove(seleccionado);
 
        try {
            GestorArchivos.escribirBitacora("ELIMINAR", seleccionado);
        } catch (IOException e) {
            mostrarError("No se pudo escribir en bitacora.txt: " + e.getMessage());
        }
    }
 
    public void limpiar() {
        campoPlaca.clear();
        campoModelo.clear();
        comboTipo.setValue(null);
        campoVelocidad.clear();
        campoPrecio.clear();
    }
 
    public void exportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar CSV");
        selector.setInitialFileName("garaje.csv");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
 
        File archivo = selector.showSaveDialog(ventana);
 
        if (archivo == null) {
            return;
        }
 
        try {
            GestorArchivos.exportarCSV(archivo, lista);
            mostrarInfo("Se exportaron " + lista.size() + " vehiculos");
        } catch (IOException e) {
            mostrarError("No se pudo exportar: " + e.getMessage());
        }
    }
 
    public void importar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Importar CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
 
        File archivo = selector.showOpenDialog(ventana);
 
        if (archivo == null) {
            return;
        }
 
        try {
            int[] resultado = GestorArchivos.importarCSV(archivo, lista);
            mostrarInfo("Cargados: " + resultado[0] + "\nDescartados: " + resultado[1]);
        } catch (IOException e) {
            mostrarError("No se pudo importar: " + e.getMessage());
        }
    }
 
    public void guardar() {
        try {
            GestorArchivos.guardarPartida(lista);
            mostrarInfo("Partida guardada en garaje.dat");
        } catch (IOException e) {
            mostrarError("No se pudo guardar la partida: " + e.getMessage());
        }
    }
 
    public void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
 
    public void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Informacion");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}