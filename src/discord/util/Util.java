package discord.util;

import java.io.FileInputStream;
//import java.time.Instant;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.util.function.Function;

public final class Util {

  public static final String readFile(String filePath) throws Exception {
    final var stream = new FileInputStream(filePath);
    final var token = new String(stream.readAllBytes());
    stream.close();
    return token;
  }

  // public static Date long_to_date(long ms) {
  //   return Date.from(Instant.ofEpochMilli(ms));
  // }

  public static final <T, U> List<U> array_map(List<T> t_list, Function<T, U> func) {
    final var u_list = new ArrayList<U>();
    for(final var t : t_list) {
      u_list.add(func.apply(t));
    }
    return u_list;
  }

}
