package com.example.liga_exam.util;

public enum ExceptionMessage {
    REMARK_FOR_BOX("В Box#%d услуги выполняются с отклонением от графика"),
    EXCEPTION_TIME("Нельзя завершать услугу до времени ее начала - %s"),
    CANCELED_ORDER("Заказ был отменен ранее, операция недоступна"),
    DONE_ORDER("Заказ был выполнен ранее, операция недоступна"),
    INVALID_INTERVAL( "Временной интервал задан неверно"),
    INVALID_DISCOUNT( "Можно назначать скидку в пределах %d% - %d%"),
    DISCOUNT_NOT_AVAILABLE( "Работнику запрещено назначать скидку"),
    INVALID_ORDER_DATE("Запись на прошедшие даты и время не доступна"),
    INVALID_ORDER_TIME("Запись допступна минимум на %d минут более текущего времени"),
    NOT_FOUND_FREE_BOXES("Нет свободных мест на выбранные дату и время"),
    REPEATED_ARRIVED("Отметка о присутсвии уже выставлена ранее"),
    INTERSECTION_ORDER_TIME("У пользователя id#%d уже создан заказ id#%d в " +
            "этот промежуток времени"),
    NOT_WORKED_BOXES("В рабочее %s время нет открытых боксов для записи"),
    NOT_WORK_IN_WEEKENDS("Автомойка не работает в выходные"),
    INVALID_ARRIVED_TIME("Подтвержать свой приезд можно не ранее чем за %d минут"),
    INVALID_DAY_ORDER("Запись доступна не более %d дней наперед"),
    REPEAT_CONFIRM("Пользователь уже подтведил бронь заказа");
    private String message;
    ExceptionMessage(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }

}
