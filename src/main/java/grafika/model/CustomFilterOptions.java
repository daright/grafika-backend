package grafika.model;

public class CustomFilterOptions {

	private int[] customFilter;

	public int[] getCustomFilter() {
		return customFilter;
	}

	public void setCustomFilter(int[] customFilter) {
		this.customFilter = customFilter;
	}

	public FilterSelection getFilterSelection() {
		return filterSelection;
	}

	public void setFilterSelection(FilterSelection filterSelection) {
		this.filterSelection = filterSelection;
	}

	private FilterSelection filterSelection;
}
