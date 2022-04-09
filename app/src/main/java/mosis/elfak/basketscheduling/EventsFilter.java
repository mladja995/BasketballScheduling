package mosis.elfak.basketscheduling;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.helpers.Utils;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventsFilter {

    private boolean _isFilterActive;
    private ArrayList<BasketballEvent> _filteredEvents;
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    private LocalDate _createdOn;
    private LocalDateTime _beginsAt;
    private LocalDateTime _endsOn;
    private int _maxNumOfPlayers;
    private int _currNumOfPlayers;
    private double _radius;

    private EventsFilter(){
        this._isFilterActive = false;
        this._filteredEvents = new ArrayList<BasketballEvent>();
        this._createdOn = null;
        this._beginsAt = null;
        this._endsOn = null;
        this._maxNumOfPlayers = -1;
        this._currNumOfPlayers = -1;
        this._radius = -1;
    }

    private static class SingletonHolder{
        public static final EventsFilter instance = new EventsFilter();
    }

    public static EventsFilter getInstance(){
        return EventsFilter.SingletonHolder.instance;
    }

    public EventsFilter setListForFiltering(ArrayList<BasketballEvent> list){
        _filteredEvents = list;
        return this;
    }

    public EventsFilter filterByCreatedOn(LocalDate dateTime){
        if (dateTime == null){
            return this;
        }
        this._isFilterActive = true;
        this._createdOn = dateTime;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            if (LocalDateTime.parse(_filteredEvents.get(i).getCreatedAt(), dateTimeFormatter).getYear() == dateTime.getYear()
                && LocalDateTime.parse(_filteredEvents.get(i).getCreatedAt(), dateTimeFormatter).getMonthValue() == dateTime.getMonthValue()
                && LocalDateTime.parse(_filteredEvents.get(i).getCreatedAt(), dateTimeFormatter).getDayOfMonth() == dateTime.getDayOfMonth()){
                _list.add(_filteredEvents.get(i));
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public EventsFilter filterByBeginsAt(LocalDateTime dateTime){
        if (dateTime == null){
            return this;
        }
        this._isFilterActive = true;
        this._beginsAt = dateTime;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            if ((LocalDateTime.parse(_filteredEvents.get(i).getBeginsAt(), dateTimeFormatter)).isAfter(dateTime)){
                _list.add(_filteredEvents.get(i));
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public EventsFilter filterByEndsOn(LocalDateTime dateTime){
        if (dateTime == null){
            return this;
        }
        this._isFilterActive = true;
        this._endsOn = dateTime;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            if (dateTime.isBefore(LocalDateTime.parse(_filteredEvents.get(i).getBeginsAt(), dateTimeFormatter))){
                _list.add(_filteredEvents.get(i));
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public EventsFilter filterByMaxNumOfPlayers(String num){
        if (num.isEmpty() || num == null){
            return this;
        }
        int _num = Integer.parseInt(num);
        this._isFilterActive = true;
        this._maxNumOfPlayers = _num;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            if (_filteredEvents.get(i).getMaxNumOfPlayers() == _num){
                _list.add(_filteredEvents.get(i));
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public EventsFilter filterByCurNumOfPlayers(String num){
        if (num.isEmpty() || num == null){
            return this;
        }
        int _num = Integer.parseInt(num);
        this._isFilterActive = true;
        this._currNumOfPlayers = _num;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            if (_filteredEvents.get(i).getCurrentNumOfPlayers() == _num){
                _list.add(_filteredEvents.get(i));
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public EventsFilter filterByRadius(double centerX, double centerY, String radius){
        if (radius.isEmpty() || radius == null){
            return this;
        }
        double _radius = Double.parseDouble(radius) * 1000;
        this._isFilterActive = true;
        this._radius = _radius;
        ArrayList<BasketballEvent> _list = new ArrayList<BasketballEvent>();
        for (int i = 0; i < _filteredEvents.size(); i++){
            BasketballEvent _event = _filteredEvents.get(i);
            if (Utils.isPointInCircle(centerX, centerY, Double.parseDouble(_event.getLatitude()), Double.parseDouble(_event.getLongitude()), _radius)){
                _list.add(_event);
            }
        }
        _filteredEvents = _list;
        return this;
    }

    public void clearFilter(){
        this._isFilterActive = false;
        this._filteredEvents = new ArrayList<BasketballEvent>();
        this._createdOn = null;
        this._beginsAt = null;
        this._endsOn = null;
        this._maxNumOfPlayers = -1;
        this._currNumOfPlayers = -1;
        this._radius = -1;
    }

    public boolean isFilterActive(){
        return this._isFilterActive;
    }

    public ArrayList<BasketballEvent> getFilteredEvents() {
        return this._filteredEvents;
    }

    public LocalDate get_createdOn() {
        return _createdOn;
    }

    public LocalDateTime get_beginsAt() {
        return _beginsAt;
    }

    public LocalDateTime get_endsOn() {
        return _endsOn;
    }

    public int get_maxNumOfPlayers() {
        return _maxNumOfPlayers;
    }

    public int get_currNumOfPlayers() {
        return _currNumOfPlayers;
    }

    public double get_radius() {
        return _radius;
    }
}

