import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import conf.Configuracion;
import dto.AlertaExclusion;
import dto.ConfAlerta;
import dto.Odd;
import dto.User;
import service.NinjaService;
import telegram.TelegramSender;
import utils.AlertaExclusionCSVUtils;
import utils.AlertasFactory;
import utils.ConfAlertasCSVUtils;
import utils.UsersUtils;

public class BotAlertas {

    // 🔹 Configuración
	public static Integer codeRespuesta = 0;
	
		
    public static void main(String[] args) {
    	
    	
        	
    	//cargamos la lista de usuarios
    	List<User> users=UsersUtils.readUsers();
    	List<AlertaExclusion> exclusiones=new ArrayList<>();
    	HashMap<Long, ConfAlerta> confAlertas=new HashMap<>();
    	
    	
    	
    	try {
			exclusiones=AlertaExclusionCSVUtils.loadFromCSV();
			confAlertas=ConfAlertasCSVUtils.loadFromCSV();
			
			if(!confAlertas.isEmpty()) {
				for (Map.Entry<Long, ConfAlerta> entry : confAlertas.entrySet()) {
		            Long clave = entry.getKey();
		            ConfAlerta valor = entry.getValue();
		            	if(valor.getRatioNivel1()<Configuracion.ratingNivel1Minimo) {
		            		Configuracion.ratingNivel1Minimo=valor.getRatioNivel1();
		            	}
		            	if(valor.getRatioNivel2()<Configuracion.ratingNivel2Minimo) {
		            		Configuracion.ratingNivel2Minimo=valor.getRatioNivel2();
		            	}
		            	
		            	if(valor.getCuotaMinima()<Configuracion.nCuotaMinima) {
		            		Configuracion.nCuotaMinima=valor.getCuotaMinima();
		            	}
		            }	
			}
			
			Configuracion.cuotaMinima=String.valueOf(Configuracion.nCuotaMinima);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

        try {
        		
            	ArrayList<Odd> lectura = new ArrayList<>();
                ArrayList<Odd> odds = new ArrayList<>();
                
                String urlParameters=NinjaService.crearUrlFiltroPeticionData(Configuracion.uid, Configuracion.filtroBookies2UP, Configuracion.ratingInicial, Configuracion.cuotaMinima, Configuracion.filtroApuestas2UP, "");
                lectura=NinjaService.mapearListaResultadosData(urlParameters, Configuracion.urlData, true);
                
                if(lectura==null) {
                	System.exit(0);
                 }
                    
                // 🔹 Leer histórico si existe
                ArrayList<Odd> oddsAnteriores = leerCSV(Configuracion.CSV_FILE);
                ArrayList<Odd> oddsGrabarCSV=new ArrayList<Odd>();
                           
    			
                            
                //filtramos eventos que no interesan
                for (Odd odd : lectura) {
                	 if (!yaExistia(odd, oddsAnteriores) && odd.getTimeInMin()<=Configuracion.FiltroMinutosAntiguedad  && pasaFiltroDatos(odd)) {
                		 
                		 //buscamos los mejores home,away y draw para cono información complementaria
                		 
                		 if("1".equals(odd.getSelectionId())) {
                			odd=NinjaService.rellenaCuotasSoloHome(odd);	 
                		 } else if ("2".equals(odd.getSelectionId())) {
                			odd=NinjaService.rellenaCuotasSoloAway(odd);
                		 } 
                		 
                		 LocalDateTime ahora=LocalDateTime.now();
                		 odd.setFechaAlerta(ahora);
                		 
                		 odds.add(odd);
                		 oddsGrabarCSV.add(odd);
                	 } else {
                		 
                		 System.out.println("ODD DESCARTADO:");
                		 System.out.println(odd.toString());
                		 System.out.println("TimeinMin: " + odd.getTimeInMin());
                		          		 
                		 
                	 }
                }
                            
                //añadimos al array grabarCSV las alertas remanentes que no se hayan renovado en esta lectura
                for (Odd oddAnterior : oddsAnteriores) {
                	boolean existe=false;
                	for (Odd oddNuevo : odds) {
                		if (oddNuevo.getEvent().equals(oddAnterior.getEvent())
                                && oddNuevo.getBookie().equals(oddAnterior.getBookie())
                                && oddNuevo.getSelection().equals(oddAnterior.getSelection())) {
                			existe=true;
                		}
                	}
    				
                	if(!existe) {
                		// no existe. COmprobamos ultimo filtro de 18 minutos para saber si hay que añadirlo al CSV o no
                		LocalDateTime ahora = LocalDateTime.now();
                		LocalDateTime fechaAlerta = oddAnterior.getFechaAlerta();
                		if (fechaAlerta.isBefore(ahora.minusMinutes(18))) {
                            System.out.println("más de 18 minutos anterior. descartamos de Anteriores");
                        } else {
                            System.out.println("está dentro de los 18 minutos. COnservamos en Anteiriores");
                            oddsGrabarCSV.add(oddAnterior);
                        }
                		
                	}
    			}
                
                
                
                if(lectura.isEmpty()) {
                	StringBuilder mensajeDebug = new StringBuilder();
                    mensajeDebug.append("<b>Debug resultados</b>\n");
                	mensajeDebug.append("La petición ha resuelto sin resultados.");
                } else if (odds.isEmpty()) {
                	StringBuilder mensajeDebug = new StringBuilder();
                    mensajeDebug.append("<b>Debug resultados</b>\n");
                	mensajeDebug.append("ningún resultado ha pasado el filtro post proceso");
                } else {
                	StringBuilder mensajeDebug = new StringBuilder();
                    mensajeDebug.append("<b>Debug resultados</b>\n");
                	mensajeDebug.append("Hay resultados post proceso a mostrar");
                }
                
               
               ArrayList<Odd> oddsFusionados=new ArrayList<Odd>();
               for (Odd odd : odds) {
    			String market_id=odd.getMarket_id();
    			boolean encontrado=false;
    			for (Odd odd2 : oddsFusionados) {
    				if(odd2.getMarket_id().equals(market_id)) {
    					Odd o=new Odd();
    					o.setBookie(odd.getBookie());
    					o.setRating(odd.getRating());
    					o.setRatingOriginal(odd.getRatingOriginal());
    					o.setBackOdd(odd.getBackOdd());
    					o.setBackOddOriginal(odd.getBackOddOriginal());
    					o.setLayOdd(odd.getLayOdd());
    					o.setSelection(odd.getSelection());
    					o.setTimeInMin(odd.getTimeInMin());
    					o.setUpdate_time(odd.getUpdate_time());
    					o.setMarket_id(odd.getMarket_id());
    					
    					odd2.getOddsFusion().add(o);
    					encontrado=true;
    				}
    			}
    			
    			if(!encontrado) {
    				
    				Odd o=new Odd();
    				o.setBookie(odd.getBookie());
    				o.setRating(odd.getRating());
    				o.setRatingOriginal(odd.getRatingOriginal());
    				o.setBackOdd(odd.getBackOdd());
    				o.setBackOddOriginal(odd.getBackOddOriginal());
    				o.setLayOdd(odd.getLayOdd());
    				o.setSelection(odd.getSelection());
    				o.setTimeInMin(odd.getTimeInMin());
    				o.setUpdate_time(odd.getUpdate_time());
    				o.setMarket_id(odd.getMarket_id());
    				
    				ArrayList<Odd> oddsFusion=new ArrayList<Odd>();
    				oddsFusion.add(o);
    				odd.setOddsFusion(oddsFusion);
    				
    				oddsFusionados.add(odd);
    				
    			}
            	   
               }
                
                
               TelegramSender.eventosFinales=oddsFusionados.size();
                
             // 🔹 Generar mensaje de Telegram (resumen)
                StringBuilder mensaje = new StringBuilder();
                
    			for (User user : users) {
    				
    				ConfAlerta confAlerta=confAlertas.get(user.getChatId());
    				if(confAlerta==null) {
    					confAlerta=new ConfAlerta();
    					confAlerta.setChatId(user.getChatId());
    					confAlerta.setRatioNivel1(Double.valueOf(Configuracion.ratingNivel1));
    					confAlerta.setRatioNivel2(Double.valueOf(Configuracion.ratingNivel2));
    					confAlerta.setCuotaMinima(Double.valueOf(Configuracion.cuotaMinimaInicial));
    				}
    							

    				//generamos el array de markets excluidos por el usuario
    				ArrayList<String> marketsExcluidos = new ArrayList<String>();
    				for (AlertaExclusion ex : exclusiones) {
    					if (user.getChatId().toString().equals(ex.getChatId().toString())) {
    						marketsExcluidos.add(ex.getMarket_id());
    					}
    				}

    				for (Odd odd : oddsFusionados) {
    					    					
    					if(!marketsExcluidos.contains(odd.getMarket_id())) {
    						
    						boolean enviar=true;
    						Double cuotaBack=Double.valueOf(odd.getBackOdd());
    						Double rating=Double.valueOf(odd.getRating());
    						
    						if(cuotaBack<5) {
    							if(rating<confAlerta.getRatioNivel1()) {
    								enviar=false;
    								System.out.println("ratio nivel1 NO pasa configuración ratio usuario cuota:"+ cuotaBack + " rating:" + rating + " ratingUsuario:" + confAlerta.getRatioNivel1()); 
    							} else {
    								System.out.println("ratio nivel1 SI pasa configuración ratio usuario cuota:"+ cuotaBack + " rating:" + rating + " ratingUsuario:" + confAlerta.getRatioNivel1());
    							}
    						} else {
    							if(rating<confAlerta.getRatioNivel2()) {
    								enviar=false;
    								System.out.println("ratio nivel2 NO pasa configuración ratio usuario cuota:"+ cuotaBack + " rating:" + rating + " ratingUsuario:" + confAlerta.getRatioNivel2()); 
    							} else {
    								System.out.println("ratio nivel2 SI pasa configuración ratio usuario cuota:"+ cuotaBack + " rating:" + rating + " ratingUsuario:" + confAlerta.getRatioNivel2());
    							}
    						}
    						
    						if(cuotaBack<confAlerta.getCuotaMinima()) {
    							enviar=false;
    							System.out.println("cuota NO pasa cuotaMinima usuario -->   cuota:"+ cuotaBack +  "cuotaMinimaUsuario;" + confAlerta.getCuotaMinima());
    						} else {
    							System.out.println("cuota SI pasa cuotaMinima usuario -->   cuota:"+ cuotaBack +  "cuotaMinimaUsuario;" + confAlerta.getCuotaMinima());
    						}
    						
    						if (enviar) {
    							// crear mensaje Alerta
        						mensaje = AlertasFactory.createAlerta(odd);
        						System.out.println("Alerta enviada");
        						// 🔹 Enviar a Telegram
        						TelegramSender.alertasEnviadas++;
        						TelegramSender.sendTelegramMessageAlerta(mensaje.toString(), odd, user.getChatId().toString());	
    						}	
    						
    					} else {
    						 System.out.println("evento excluido por el usuario " + user.getChatId() + " -->" + odd.getEvent());
    						 System.out.println("no lo enviamos");
    					}
    					
    					

    				}

    			}
                                
                
                // 🔹 Guardar los odds actuales como histórico
                escribirCSV(Configuracion.CSV_FILE, oddsGrabarCSV);
                
                //borrar exclusiones de alertas cuyos eventos ya han pasado
                List<AlertaExclusion> exclusionesFiltradas=AlertaExclusionCSVUtils.filtrarAlertasPosteriores(exclusiones);
                AlertaExclusionCSVUtils.escribirAlertasEnCsv(exclusionesFiltradas);
        	
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			
	        StringBuilder mensajeDebug = new StringBuilder();
	        mensajeDebug.append("<b>Debug Ejecucion</b>\n\n");
	        
	        mensajeDebug.append("HTTP 200 Inicial: <b>").append(TelegramSender.response200_Inicial).append("</b>\n");
	        mensajeDebug.append("HTTP 200 Events: <b>").append(TelegramSender.response200_Events).append("</b>\n");
	        mensajeDebug.append("HTTP 200 Adicional: <b>").append(TelegramSender.response200_Adicional).append("</b>\n");
	        mensajeDebug.append("Peticiones Exchange: <b>").append(TelegramSender.peticionesAExchange).append("</b>\n");
	        mensajeDebug.append("HTTP 403 Adicional: <b>").append(TelegramSender.response403).append("</b>\n");
	        mensajeDebug.append("Eventos Iniciales: <b>").append(TelegramSender.eventosIniciales).append("</b>\n");
	        mensajeDebug.append("Eventos Finales: <b>").append(TelegramSender.eventosFinales).append("</b>\n");
	        mensajeDebug.append("Eventos Alertas enviadas: <b>").append(TelegramSender.alertasEnviadas).append("</b>\n");
	        mensajeDebug.append("Alertas fallidas: <b>").append(TelegramSender.response400Telegram).append("</b>\n");
	      
	       TelegramSender.sendTelegramMessageDebug(mensajeDebug.toString());
        	
        	
		}
              
        System.out.println("FIN EJECUCION");
    }
    
    

    
    private static boolean pasaFiltroDatos(Odd odd) {
    	
    	
    	//filtro Paises
    	ArrayList<String> filtroPaises=new ArrayList<String>();
    	filtroPaises.add("Argentina");
    	filtroPaises.add("Saudi Arabia");
    	filtroPaises.add("Arabia Saudí");
    	filtroPaises.add("Brasil");
    	filtroPaises.add("Estados Unidos");
    	filtroPaises.add("México");
    	filtroPaises.add("Mexico");
    	filtroPaises.add("Bolivia");
    	
    	   	
    	if(filtroPaises.contains(odd.getCountry())) {
    		System.out.println("Evento no pasa filtro pais --> " + odd.getCountry());
    		return false;
    	}    	
    	
    	
    	Double rating=Double.valueOf(odd.getRating());
    	Double cuota=Double.valueOf(odd.getBackOdd());
    	
    	//filtro cuota demasiado Alta
    	if(cuota>10) {
    		System.out.println("Evento no pasa filtro cuota BACK demasiado alta --> " + odd.getRating() + "/" + odd.getBackOdd());
    		return false;
    	}
    	
    	//filtro rating
    	if(cuota<5) {
    		if(rating<Configuracion.ratingNivel1Minimo) {
    			System.out.println("Evento no pasa filtro rating/cuota NIVEL 1 --> " + odd.getRating() + "/" + odd.getBackOdd());
    			return false;
    		}
    	} else {
    		if(rating<Configuracion.ratingNivel2Minimo) {
    			System.out.println("Evento no pasa filtro rating/cuota NIVEL 2 --> " + odd.getRating() + "/" + odd.getBackOdd());
    			return false;
    		}
    	}
    	
    	
    	// Hay un primer filtro de rating en la búsqueda que es el mínimo aqui se contrastan cuotas con ratings
//    	if(cuota<5 && rating<95) {
//    		System.out.println("Evento no pasa filtro rating/cuota --> " + odd.getRating() + "/" + odd.getBackOdd());
//    		return false;
//    	}
    	    	
    	
    	    	
    	
    	//filtro partido demasiado lejano
    	LocalDateTime ahora = LocalDateTime.now();
    	LocalDateTime fechaObjetivo=odd.getFechaPartido();
    	long diferencia = ChronoUnit.DAYS.between(ahora, fechaObjetivo);

        if (Math.abs(diferencia) <= 5) {
            System.out.println("✅ La fecha está dentro de ±5 días de hoy");
        } else {
            System.out.println("❌ La fecha está fuera del rango de ±5 días");
            return false;
        } 
    	
    	    	
    	return true;
    }

    // 🔹 Método para comprobar si ya existía en el histórico
    private static boolean yaExistia(Odd nuevo, ArrayList<Odd> anteriores) {
        for (Odd o : anteriores) {
            if (o.getEvent().equals(nuevo.getEvent())
                    && o.getBookie().equals(nuevo.getBookie())
                    && o.getSelection().equals(nuevo.getSelection())) {
            	
            	if(o.getRating().equals(nuevo.getRating())) {
            		return true;
            	} else {
            		
            		Double ratingExistente=Double.valueOf(o.getRating());
            		Double ratingNuevo=Double.valueOf(nuevo.getRating());
            		
            		if(ratingNuevo<ratingExistente) {
            			//el nuevo rating es peor que el que ya habíamos lanzado anteriormente, devolvemos true para que se descarte la alerta
            			return true;
            		}
            		            		
            	}
            	               
            }
                    
        }
        return false;
    }

    // 🔹 Guardar odds en CSV
    private static void escribirCSV(String file, ArrayList<Odd> odds) {
    	
    	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    	 
    	
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Odd o : odds) {
            	String fechaFormateada = o.getFechaAlerta().format(formatter);
                pw.println(String.join(";",
                        o.getEvent(), o.getBookie(), o.getRating(), o.getBackOdd(),
                        o.getLayOdd(), o.getSelection(), o.getCompetition(),
                        o.getUpdate_time(), o.getCountry(), o.getTimeInMin().toString(), fechaFormateada));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Leer odds desde CSV
    private static ArrayList<Odd> leerCSV(String file) {
        ArrayList<Odd> lista = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return lista;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] campos = line.split(";");
                if (campos.length >= 10) {
                    Odd o = new Odd();
                    o.setEvent(campos[0]);
                    o.setBookie(campos[1]);
                    o.setRating(campos[2]);
                    o.setBackOdd(campos[3]);
                    o.setLayOdd(campos[4]);
                    o.setSelection(campos[5]);
                    o.setCompetition(campos[6]);
                    o.setUpdate_time(campos[7]);
                    o.setCountry(campos[8]);
                    o.setTimeInMin(Integer.valueOf(campos[9]));
                    LocalDateTime fecha = LocalDateTime.parse(campos[10], formatter);
                    o.setFechaAlerta(fecha);
                    lista.add(o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    
      

    



    

}
