/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public class ReverseFunction extends StringFunction {

  public String execute(String data) {
    StringBuffer sb = new StringBuffer(data);
    sb = sb.reverse();
    return sb.toString();
  }

  public String getName() {
    return "reverse";
  }

}
