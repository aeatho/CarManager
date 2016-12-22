/**
 * Project Name:Android_Car_Example
 * File Name:PoiSearchTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月7日上午11:25:07
 */

package com.unovo.carmanager.common.lbs;

import android.content.Context;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.unovo.carmanager.R;
import com.unovo.carmanager.common.map.ToastUtil;
import com.unovo.carmanager.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:PoiSearchTask <br/>
 * Function: 简单封装了poi搜索的功能，搜索结果配合RecommendAdapter进行使用显示 <br/>
 * Date: 2015年4月7日 上午11:25:07 <br/>
 *
 * @author yiyi.qi
 * @see
 * @since JDK 1.6
 */
public class PoiSearchTask implements OnPoiSearchListener {

  private Context mContext;

  private OnPoiGetListener onPoiGetListener;

  public interface OnPoiGetListener {
    void onPoiSearchGet(PositionEntity entity);

    void onPoiSearchGetFailed();
  }

  public void setOnLocationGetListener(OnPoiGetListener onGetLocationListener) {
    onPoiGetListener = onGetLocationListener;
  }

  public PoiSearchTask(Context context) {
    mContext = context;
  }

  public void search(String keyWord, String city) {
    Query query = new Query(keyWord, "", city);
    query.setPageSize(10);
    query.setPageNum(0);

    PoiSearch poiSearch = new PoiSearch(mContext, query);
    poiSearch.setOnPoiSearchListener(this);
    poiSearch.searchPOIAsyn();
  }

  @Override public void onPoiItemSearched(PoiItem poiItem, int i) {

  }

  @Override public void onPoiSearched(PoiResult poiResult, int resultCode) {
    if (resultCode == 1000) {
      if (poiResult != null && poiResult.getPois() != null && !poiResult.getPois().isEmpty()) {
        ArrayList<PoiItem> pois = poiResult.getPois();
        List<PositionEntity> entities = new ArrayList<PositionEntity>();
        for (PoiItem poiItem : pois) {
          PositionEntity entity = new PositionEntity(poiItem.getLatLonPoint().getLatitude(),
              poiItem.getLatLonPoint().getLongitude(), poiItem.getTitle(), poiItem.getCityName());
          entities.add(entity);
        }

        if (onPoiGetListener != null) {
          onPoiGetListener.onPoiSearchGet(entities.get(0));
        }
      } else {
        if (onPoiGetListener != null) onPoiGetListener.onPoiSearchGetFailed();
        ToastUtil.show(UIUtils.getContext(), R.string.no_result);
      }
    } else {
      if (onPoiGetListener != null) onPoiGetListener.onPoiSearchGetFailed();
      ToastUtil.showerror(UIUtils.getContext(), resultCode);
    }
  }
}
