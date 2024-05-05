package r2u.tools.utils;

import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;

import java.util.Iterator;

public class DataFetcher {
    public static Iterator<?> fetchRows(String docClass, ObjectStore objectStoreSource) {
        String querySource = "SELECT * FROM " + docClass;
        SearchSQL searchSQL = new SearchSQL();
        searchSQL.setQueryString(querySource);
        return new SearchScope(objectStoreSource).fetchRows(searchSQL, null, null, Boolean.TRUE).iterator();
    }
}
