package Folder.Common;

import java.util.List;

public interface IAutocompleteHandler {
    List<String> getFilteredEntries(String searchText);
    void populateEntires(List<String> items);
}
