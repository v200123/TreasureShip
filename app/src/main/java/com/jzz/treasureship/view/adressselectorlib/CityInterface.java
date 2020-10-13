package com.jzz.treasureship.view.adressselectorlib;

model.bean.CityPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Blincheng.
 * Date: 2017/5/9.
 * Description:
 */

public interface CityInterface {
    String getCityAllName();
    String getCityAreaName();
    ArrayList<CityPlace> getCityChildren();
    int getCityId();
    String getCityInital();
    int getChildIsTop();
    int getCityParentId();
    String getChildSpell();
}
