import java.util.List;

public  class Exchange {
        private List<PriceSize> availableToBack;
        private List<PriceSize> availableToLay;
		public List<PriceSize> getAvailableToBack() {
			return availableToBack;
		}
		public void setAvailableToBack(List<PriceSize> availableToBack) {
			this.availableToBack = availableToBack;
		}
		public List<PriceSize> getAvailableToLay() {
			return availableToLay;
		}
		public void setAvailableToLay(List<PriceSize> availableToLay) {
			this.availableToLay = availableToLay;
		}
}