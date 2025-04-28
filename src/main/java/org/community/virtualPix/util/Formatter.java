package org.community.virtualPix.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formatter {

    public static DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
    public static DecimalFormat df = new DecimalFormat("#,##0.00", symbols);


}
