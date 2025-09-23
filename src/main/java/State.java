    public  class State {
        private int sortPriority;
        private double lastPriceTraded;
        private double totalMatched;
        private String status;
		public int getSortPriority() {
			return sortPriority;
		}
		public void setSortPriority(int sortPriority) {
			this.sortPriority = sortPriority;
		}
		public double getLastPriceTraded() {
			return lastPriceTraded;
		}
		public void setLastPriceTraded(double lastPriceTraded) {
			this.lastPriceTraded = lastPriceTraded;
		}
		public double getTotalMatched() {
			return totalMatched;
		}
		public void setTotalMatched(double totalMatched) {
			this.totalMatched = totalMatched;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

        
    }