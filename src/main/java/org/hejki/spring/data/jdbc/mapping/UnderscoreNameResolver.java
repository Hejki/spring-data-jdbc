package org.hejki.spring.data.jdbc.mapping;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public class UnderscoreNameResolver implements NameResolver {
    @Override
    public String resolve(String name) {
        if (null == name) {
            return null;
        }

        int len = name.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);

            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
