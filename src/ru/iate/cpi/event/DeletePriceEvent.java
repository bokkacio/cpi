package ru.iate.cpi.event;

/**
 * Created by sanea on 04.06.15.
 */
public class DeletePriceEvent {
    final int PriceId;

    public DeletePriceEvent(int priceId){
        this.PriceId = priceId;
    }
}
