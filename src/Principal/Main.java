package Principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author kyron
 */
public class Main {
    public static String nombreArchivo = "";
    public static Scanner lector = new Scanner(System.in);
    public static List<String> codigoLineas = new ArrayList<>();
    public static List<String> cadenasCodigo = new ArrayList<>();
    public static List<String> tablaSimbolos = new ArrayList<>();
    public static List<String> tabSimENTEROS = new ArrayList<>();
    public static List<String> tabSimFLOTANTES = new ArrayList<>();
    public static List<String> tabSimBOLEANOS = new ArrayList<>();
    public static List<String> tabSimCADENAS = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("COMPILADOR DEL LENGUAJE KIRAFEL (.krfl)\nDesarrollado por: Kevin Roberto Flores Leon\n");
        
        lectorNombres();
        
        if (nombreArchivo.isEmpty()){
            nombreArchivo = obtenerNombreArchivo();
            guardadoNombre(nombreArchivo);
            lectorCodigo(nombreArchivo+".krfl");
        } else {
            System.out.println("Archivo: "+nombreArchivo+"\nDesea utilizar otro archivo? [Y/cualquier otro input]");
            String opcion = lector.nextLine();
            
            switch (opcion){
                case "Y":
                    nombreArchivo = obtenerNombreArchivo();
                    guardadoNombre(nombreArchivo);
                    lectorCodigo(nombreArchivo+".krfl");
                    break;
                case "y":
                    nombreArchivo = obtenerNombreArchivo();
                    guardadoNombre(nombreArchivo);
                    lectorCodigo(nombreArchivo+".krfl");
                    break;
                default:
                    lectorCodigo(nombreArchivo+".krfl");
                    break;
            }
        }        

        boolean errores = false;

        if (!AnLexico.AnalizadorLexico()) errores = true;
        
        if (!AnSintactico.AnalizadorSintactico()) errores = true;
        
        if (!AnSemantico.AnalizadorSemantico()) errores = true;

        if (errores){
            System.exit(0);
        } else GeneradorCodigo.generadorCodigo();

//        System.out.println(codigoLineas);
    }
    
    /**
     *
     * @param
     */
    public static void lectorCodigo(String fichero){
        try (FileReader fr = new FileReader(fichero)) {
            BufferedReader br = new BufferedReader(fr);
            String linea;
            int contador = 1;
            while((linea=br.readLine()) != null){
                if(!linea.isEmpty()) codigoLineas.add(contador + ";" + linea);
                contador++;
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static void lectorNombres(){
        try (FileReader fr = new FileReader("ruta/nombre")) {
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while((linea=br.readLine()) != null){
                if(!linea.isEmpty()){
                    nombreArchivo = linea;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void guardadoNombre(String nombre){
        try {
            PrintWriter writer = new PrintWriter("ruta/nombre", "UTF-8");
            writer.println(nombre);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String obtenerNombreArchivo(){
        String nombre = "";
        
        boolean validar = false;
        while (!validar){
            System.out.println("Ingrese el nombre del archivo que contiene el codigo");
            nombre = lector.nextLine();

            if (nombre.matches("[A-Za-z0-9]+")){
                validar = true;
            } else {
                System.out.println("[ERROR] Nombre invalido\nsolo se aceptan caracteres alfanumericos");
            }
        }
        
        return nombre;
    }
}
