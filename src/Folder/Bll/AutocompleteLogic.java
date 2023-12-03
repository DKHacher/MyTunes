package Folder.Bll;

import Folder.Common.IAutocompleteHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AutocompleteLogic implements IAutocompleteHandler {
    private final List<String> entries;

    public AutocompleteLogic() {
        entries = new ArrayList<>();
    }

    @Override
    public List<String> getFilteredEntries(String searchText) {
        return entries.stream()
                      .filter(e -> e.toLowerCase().contains(searchText.toLowerCase()))
                      .collect(Collectors.toList());
    }

    @Override
    public void populateEntires(List<String> items) {
        entries.clear();
        entries.addAll(items);
    }
}
