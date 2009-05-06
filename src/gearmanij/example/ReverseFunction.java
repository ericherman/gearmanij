/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import gearmanij.StringFunction;

public class ReverseFunction extends StringFunction {
  
  private int delay = 0;

  public int getDelay() {
    return delay;
  }

  /**
   * Set delay in seconds before the String is reversed. Useful for
   * testing CAN_DO_TIMEOUT packet type.
   * 
   * @param delay
   */
  public void setDelay(int delay) {
    this.delay = delay;
  }

  public String execute(String data) {
    try {
      Thread.sleep(delay * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    StringBuffer sb = new StringBuffer(data);
    sb = sb.reverse();
    return sb.toString();
  }

  public String getName() {
    return "reverse";
  }

}
