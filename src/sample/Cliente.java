package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import Listas.Esquema;

public class Cliente {
    private static Logger log = LoggerFactory.getLogger(Controller.class);
    private ObjectMapper objectMapper=new ObjectMapper();
    private int puerto;
    private InetAddress IP;
    private Datos datos=new Datos();
    public Cliente() {
        try {
    		this.puerto  = convertPropertyToInt(this.getPropertyKey("puerto"));
            IP=InetAddress.getLocalHost();
            System.out.println(IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
	/*//metodo para obtener del property el dato guardado con ese key*/
	public String getPropertyKey(String key) {
		return ReadPropertyFile.main(key);
	}
	private int convertPropertyToInt(String key) {
		return Integer.parseInt(key);	
	}
	/*____*/

    public String crearEsquema(String constructor){
        this.datos.setAccion("crear esquema");
        this.datos.setDatos(constructor);
        return this.conectar().getRespuesta();
    }

    public String eliminarEsquema(String nombre_esquema) {
        this.datos.setAccion("eliminar esquema");
        this.datos.setNombre(nombre_esquema);
        return this.conectar().getRespuesta();
    }

    public String insertardatos(String nombre_esquema,String datos){
        this.datos.setAccion("insertar datos");
        this.datos.setNombre(nombre_esquema);
        this.datos.setDatos(datos);//constructor de datos
        return this.conectar().getRespuesta();
    }
    public String eliminardatos(String nombre_esquema,String ID){//ID es la primera columna del esquema
        this.armarobjetodatos("eliminar datos",nombre_esquema,ID,"","",null);
        return this.conectar().getRespuesta();
    }

    public Datos buscardatos(String nombre_esquema,String dato,String columna){
        this.armarobjetodatos("buscar datos",nombre_esquema,dato,columna,"",null);
        return this.conectar();
    }
    public Datos buscardatosporjoin(String nombre_esquema,String dato,String columna,String nombre_joins){//usado si el parametro de busqueda es por el de un dato en un join que no sea el ID
        this.armarobjetodatos("buscar datos por join",nombre_esquema,dato,columna,"",nombre_joins);
        return this.conectar();//poner el nombre del esquema de la columna osea el ultimo join
    }
    public Datos buscardatosporindice(String nombre_esquema,String dato,String columna,String indice){
        this.armarobjetodatos("buscar datos por indice",nombre_esquema,dato,columna,indice,null);
        return this.conectar();
    }
    public String crearindice(String nombre_esquema,String columna,String indice){
        this.armarobjetodatos("crear indice",nombre_esquema,"",columna,indice,null);
        return this.conectar().getRespuesta();
    }
    public String eliminarindice(String nombre_esquema,String columna,String indice){
        this.armarobjetodatos("eliminar indice",nombre_esquema,"",columna,indice,null);
        return this.conectar().getRespuesta();
    }
    public String cambiarnombreesquema(String Nombre, String nuevoNombre){
        this.datos.setAccion("Cambiar nombre de Esquema");
        this.datos.setNombre(Nombre);
        this.datos.setCambio(nuevoNombre);
        return conectar().getRespuesta();
    }
    public String cambiardato(String Nombre_esquema,String ID,String Columna, String Nuevodato){
        this.datos.setAccion("cambiar dato");
        this.datos.setNombre(Nombre_esquema);
        this.datos.setDato(ID);
        this.datos.setColumna(Columna);
        this.datos.setCambio(Nuevodato);
        return conectar().getRespuesta();
    }
    public Datos recibirEsquemas(){
        this.datos.setAccion("enviar esquemas");
        return conectar();
    }
    public Datos buscartodoslosdatos(String Nombre_esquema){
        this.datos.setNombre(Nombre_esquema);
        this.datos.setAccion("buscar todos los datos");
        return conectar();
    }
    public String guardarDatos(){
        this.datos.setAccion("guardar datos");
        return conectar().getRespuesta();
    }
    public Datos buscardatosparaedit(String Nombre_esquema){
        this.datos.setNombre(Nombre_esquema);
        this.datos.setAccion("buscar datos para edit");
        return conectar();
    }

    private void armarobjetodatos(String accion,String nombre,String dato,String columna, String indice, String nombre_join){
        this.datos.setAccion(accion);
        this.datos.setNombre(nombre);
        this.datos.setDato(dato);
        this.datos.setColumna(columna);
        this.datos.setIndice(indice);
        this.datos.setNombre_join(nombre_join);
    }
    private Datos conectar()  {
        Datos datosrecibidos=new Datos();
        try {
            Socket client = new Socket(IP, puerto);
            log.debug("unirse");
            DataOutputStream datosenvio = new DataOutputStream(client.getOutputStream());
            datosenvio.writeUTF(objectMapper.writeValueAsString(this.datos));
            log.debug("se envio datos");
            DataInputStream datosentrada = new DataInputStream(client.getInputStream());
            log.debug("entrada se conecto");
            datosrecibidos = objectMapper.readValue(datosentrada.readUTF(), Datos.class);
            log.debug("se creo objeto");
            datosenvio.close();
            client.close();

        } catch (IOException e) {
            datosrecibidos.setRespuesta("error al conectar");
        }
        return datosrecibidos;
    }
}
