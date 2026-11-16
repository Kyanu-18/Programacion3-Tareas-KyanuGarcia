
import java.io.Serializable;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String placa;
    private String modelo;
    private String tipo;
    private int velocidadMax;
    private int precio;

    public Vehiculo(String placa, String modelo, String tipo, int velocidadMax, int precio) {
        this.placa = placa;
        this.modelo = modelo;
        this.tipo = tipo;
        this.velocidadMax = velocidadMax;
        this.precio = precio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getVelocidadMax() {
        return velocidadMax;
    }

    public void setVelocidadMax(int velocidadMax) {
        this.velocidadMax = velocidadMax;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String toString() {
        return placa + "|" + modelo + "|" + tipo + "|" + velocidadMax + "|" + "km/h|$" + precio;
    }
    
}
