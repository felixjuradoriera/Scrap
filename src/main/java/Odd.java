    // 🔹 Clase Odd

import java.time.LocalDateTime;
import java.util.ArrayList;


public class Odd {
        private String event;
        private String bookie;
        private String rating;
        private String backOdd;
        private String layOdd;
        private String selection;
        private String competition;
        private String update_time;
        private String country;
        private Integer timeInMin;
        
        private String backOddOriginal;
        private String ratingOriginal;
        
        private LocalDateTime fechaPartido;
        private String sFechaPartido;
        
        private String market_id;
        
        ArrayList<Odd> mejoresHome=new ArrayList<Odd>();
        ArrayList<Odd> mejoresDraw=new ArrayList<Odd>();
        ArrayList<Odd> mejoresAway=new ArrayList<Odd>();
        
        Odd exchangeDraw;
        
        Integer nivelAlerta;
        
        String equipoHome="";
        String equipoAway="";
        
        private LocalDateTime fechaAlerta;

        // getters y setters
        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        public String getBookie() { return bookie; }
        public void setBookie(String bookie) { this.bookie = bookie; }
        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
        public String getBackOdd() { return backOdd; }
        public void setBackOdd(String backOdd) { this.backOdd = backOdd; }
        public String getLayOdd() { return layOdd; }
        public void setLayOdd(String layOdd) { this.layOdd = layOdd; }
        public String getSelection() { return selection; }
        public void setSelection(String selection) { this.selection = selection; }
        public String getCompetition() { return competition; }
        public void setCompetition(String competition) { this.competition = competition; }
        public String getUpdate_time() { return update_time; }
        public void setUpdate_time(String update_time) { this.update_time = update_time; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
		public Integer getTimeInMin() {
			return timeInMin;
		}
		public void setTimeInMin(Integer timeInMin) {
			this.timeInMin = timeInMin;
		}
		public String getMarket_id() {
			return market_id;
		}
		public void setMarket_id(String market_id) {
			this.market_id = market_id;
		}
		public String getBackOddOriginal() {
			return backOddOriginal;
		}
		public void setBackOddOriginal(String backOddOriginal) {
			this.backOddOriginal = backOddOriginal;
		}
		public String getRatingOriginal() {
			return ratingOriginal;
		}
		public void setRatingOriginal(String ratingOriginal) {
			this.ratingOriginal = ratingOriginal;
		}
		public ArrayList<Odd> getMejoresHome() {
			return mejoresHome;
		}
		public void setMejoresHome(ArrayList<Odd> mejoresHome) {
			this.mejoresHome = mejoresHome;
		}
		public ArrayList<Odd> getMejoresDraw() {
			return mejoresDraw;
		}
		public void setMejoresDraw(ArrayList<Odd> mejoresDraw) {
			this.mejoresDraw = mejoresDraw;
		}
		public ArrayList<Odd> getMejoresAway() {
			return mejoresAway;
		}
		public void setMejoresAway(ArrayList<Odd> mejoresAway) {
			this.mejoresAway = mejoresAway;
		}
		public String getEquipoHome() {
			return equipoHome;
		}
		public void setEquipoHome(String equipoHome) {
			this.equipoHome = equipoHome;
		}
		public String getEquipoAway() {
			return equipoAway;
		}
		public void setEquipoAway(String equipoAway) {
			this.equipoAway = equipoAway;
		}
		
		public String getsFechaPartido() {
			return sFechaPartido;
		}
		public void setsFechaPartido(String sFechaPartido) {
			this.sFechaPartido = sFechaPartido;
		}
		public LocalDateTime getFechaPartido() {
			return fechaPartido;
		}
		public void setFechaPartido(LocalDateTime fechaPartido) {
			this.fechaPartido = fechaPartido;
		}
		public Odd getExchangeDraw() {
			return exchangeDraw;
		}
		public void setExchangeDraw(Odd exchangeDraw) {
			this.exchangeDraw = exchangeDraw;
		}
		public Integer getNivelAlerta() {
			return nivelAlerta;
		}
		public void setNivelAlerta(Integer nivelAlerta) {
			this.nivelAlerta = nivelAlerta;
		}
		public LocalDateTime getFechaAlerta() {
			return fechaAlerta;
		}
		public void setFechaAlerta(LocalDateTime fechaAlerta) {
			this.fechaAlerta = fechaAlerta;
		}
	
		
        
    }