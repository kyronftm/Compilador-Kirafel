package Principal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kyron
 */
public class AnLexico {
    public static final String VARIABLES = "ENTERO|FLOTANTE|CADENA|BOLEANO";
    public static final String LENGUAJE = "LEER|IMPRIMIR|SI|DELOCONTRARIO|PARA|MIENTRAS|VERDADERO|FALSO|Y|O";
    public static final String SIMBOLOS = "\\(|\\)|\\+|-|\\*|/|%|=|!|>|<|\\.|\\{|\\}|\\^|\\&|\\|";
    public static final String IDENTIFICADORES = "[A-Za-z]+[A-Za-z0-9]*";
    public static final String CADENAS = "\\'\\d+\\'";
    public static final String NUMERO = "[0-9]+";
    public static List<String> lineaTokens = new ArrayList<>();
    public static String numLinea = "";
    
    public static boolean AnalizadorLexico(){
        boolean resultado = true;
        String linea = "", auxiliar = "";
        
        for (int i = 0; i < Main.codigoLineas.size(); i++){
            linea = Main.codigoLineas.get(i);
            auxiliar = eliminaEspacios(linea);
            Main.codigoLineas.set(i, auxiliar);
        }
        Main.codigoLineas = eliminaLineasVacias((ArrayList<String>) Main.codigoLineas);
        
        for (String linea4Tokens : Main.codigoLineas){
            lineaTokens.clear();
            tokenizador(linea4Tokens);
            String IDs = String.join("|", Main.tablaSimbolos);
            for (String token : lineaTokens){
                if (!token.matches(VARIABLES + "|" + LENGUAJE + "|" + SIMBOLOS + "|" + CADENAS + "|" + NUMERO + "|" + IDs)){
                    resultado = false;
                    System.out.println("[EL - 01] ERROR en el token \"" + token + "\" en la linea "+ numLinea + ": TOKEN INVALIDO");
                }
            }
        }
        if (resultado){
            for (String lineaTabSim : Main.codigoLineas){
                int inicioLinea = lineaTabSim.indexOf(';') + 1;
                auxiliar = lineaTabSim.substring(inicioLinea);
                numLinea = lineaTabSim.substring(0, inicioLinea - 1);
                if (auxiliar.matches("("+VARIABLES+")\\(("+String.join("|", Main.tablaSimbolos)+")\\)")) tabSimTipo(auxiliar);
            }
        }
        
        return resultado;
    }
    
    public static void tabSimTipo(String linea){
        String tipo = linea.substring(0, linea.indexOf('('));
        String id = linea.substring(linea.indexOf('(')+1, linea.length()-1);
        switch (tipo){
            case "ENTERO":
                Main.tabSimENTEROS.add(id);
                break;
            case "FLOTANTE":
                Main.tabSimFLOTANTES.add(id);
                break;
            case "BOLEANO":
                Main.tabSimBOLEANOS.add(id);
                break;
            case "CADENA":
                Main.tabSimCADENAS.add(id);
                break;
        }
    }
    
    public static void tokenizador(String linea){
        String token = "", auxiliar = "";
        
        int inicioLinea = linea.indexOf(';') + 1;
        auxiliar = linea.substring(inicioLinea);
        numLinea = linea.substring(0, inicioLinea - 1);
        
        boolean deteccionID = false;
        int inicioID = 0;
        for (int n = 0; n < auxiliar.length(); n++){
            token += auxiliar.charAt(n);
            if (token.matches("("+VARIABLES+")\\(") && deteccionID == false){
                token = "";
                deteccionID = true;
                inicioID = n;
            }
            if (token.matches(IDENTIFICADORES + "\\)") && deteccionID){
                //System.out.println(token);
                token = token.substring(0, n - inicioID - 1);
                Main.tablaSimbolos.add(token);
                break;
            }
            
        }
        token = "";
        
        for (int m = 0; m < auxiliar.length(); m++){
            token += auxiliar.charAt(m);
            if ((""+auxiliar.charAt(m)).matches(SIMBOLOS)){
                if (token.length() > 1){
                    lineaTokens.add(token.substring(0, token.length() - 1));
                    lineaTokens.add(token.substring(token.length() - 1));
                    token = "";
                } else {
                    lineaTokens.add(token);
                    token = "";
                }
            } else if (m == auxiliar.length() - 1){
                lineaTokens.add(token);
                token = "";
            }
        }
        
    }
    
    public static ArrayList<String> eliminaLineasVacias(ArrayList<String> lista){
        ArrayList<String> auxiliar = new ArrayList<String>();
        auxiliar.addAll(lista);
        for (String linea : auxiliar){
            if (linea.matches("[0-9]+;")){
                lista.remove(lista.indexOf(linea));
            }
        }
        return lista;
    }
    
    public static String eliminaEspacios(String linea){
        linea = detectarCadena(linea);
        String auxiliar = "";
        for (char caracter : linea.toCharArray()) 
            if (caracter != '\s') auxiliar += caracter;
        return auxiliar;
    }
    
    public static String detectarCadena(String linea){
        String cadena = "", auxiliar = "";
        boolean esCadena = false;
        short usoEscape = 0;
        for (int j = 0; j < linea.length(); j++) {
            char caracter = linea.charAt(j);
            if (usoEscape == 0){
                if (caracter == '\\') usoEscape = 2;
            } else {
                usoEscape--;
            }
            if (caracter == '\''){
                if (esCadena){
                    if (usoEscape == 0){
                        esCadena = false;
                        Main.cadenasCodigo.add(cadena);
                        cadena = "";
                        auxiliar += "\'"+(Main.cadenasCodigo.size()-1)+"\'";
                    }else cadena += caracter;
                } else esCadena = true;
            } else {
                if (esCadena){
                    if (!(usoEscape == 2))
                    cadena += caracter;
                } else {
                    auxiliar += caracter;
                }
            }
            if (caracter == '#'){
                if (esCadena && (usoEscape != 0)){
                    cadena += caracter;
                } else {
                    auxiliar = auxiliar.substring(0, j);
                    return auxiliar;
                }
            }
        }
        return auxiliar;
    }
}
