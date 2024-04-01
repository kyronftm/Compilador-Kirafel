package Principal;

import java.util.Stack;

/**
 *
 * @author kyron
 */
public class AnSemantico {
    public static Stack<String> pilaSegmentos = new Stack<>();
    public static Stack<String> pilaNumFilaInicioSegmentos = new Stack<>();
    
    public static boolean AnalizadorSemantico(){
        boolean resultado = true;
        String auxiliar = "", numLinea = "";
        Main.tablaSimbolos.clear();
        
        for (String linea : Main.codigoLineas){
            int inicioLinea = linea.indexOf(';') + 1;
            auxiliar = linea.substring(inicioLinea);
            numLinea = linea.substring(0, inicioLinea - 1);
            
            if (auxiliar.matches("("+AnLexico.VARIABLES+")\\(("+AnLexico.IDENTIFICADORES+")\\)") && deteccionIdDuplicada(auxiliar)){
                System.out.println("[EM - 04] ERROR en la linea "+numLinea+" -> "+auxiliar+": IDENTIFICADOR DUPLICADO");
                resultado = false;
            }
            
            try{
                String sentencia = auxiliar.substring(0, auxiliar.indexOf('('));
                if (sentencia.matches("SI|PARA|MIENTRAS")){
                    pilaSegmentos.push(sentencia);
                    pilaNumFilaInicioSegmentos.push(numLinea);
                }
            } catch (Exception e){
                if (auxiliar.matches("\\}DELOCONTRARIO\\{")){
                    if (!pilaSegmentos.isEmpty() && pilaSegmentos.peek().matches("SI")){
                        pilaSegmentos.pop();
                        pilaNumFilaInicioSegmentos.pop();
                        pilaSegmentos.push("DELOCONTRARIO");
                        pilaNumFilaInicioSegmentos.push(numLinea);
                    } else {
                        System.out.println("[EM - 07] ERROR en la linea "+numLinea+" -> "+auxiliar+": DELOCONTRARIO UTILIZADO SIN LA SENTENCIA \"SI\"");
                        resultado = false;
                    }
                }
                if (auxiliar.matches("\\}")){
                    if (pilaSegmentos.isEmpty()){
                        System.out.println("[EM - 06] ERROR en la linea "+numLinea+" -> "+auxiliar+": CIERRE DE SEGMENTO SOBRANTE \"SI\"");
                        resultado = false;
                    } else {
                        pilaSegmentos.pop();
                        pilaNumFilaInicioSegmentos.pop();
                    }
                }
            }
        }
        if (!pilaSegmentos.isEmpty()){
            System.out.println("[EM - 05] ERROR en la linea "+pilaNumFilaInicioSegmentos.peek()+" -> "+pilaSegmentos.peek()+": SEGMENTO SIN CERRAR");
            resultado = false;
        }
        return resultado;
    }
    
    public static boolean deteccionIdDuplicada(String linea){
        boolean resultado = false;
        
        String id = linea.substring(linea.indexOf('(')+1, linea.length()-1);
        if (id.matches("("+String.join("|", Main.tablaSimbolos)+")")){
            resultado = true;
        }else{
            Main.tablaSimbolos.add(id);
        }
        return resultado;
    }
    
    
}
