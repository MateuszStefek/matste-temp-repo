package matste.util;

import matste.util.StringExtensionsKt;

public class KotlinInteropExample {

    public static String useKotlinExtension(String input) {
        return StringExtensionsKt.toSnakeCase(input);
    }
}

