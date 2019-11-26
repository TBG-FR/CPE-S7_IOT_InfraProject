package cpe.iot.tocards.androidapp.dndrv_helpers;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}