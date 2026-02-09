package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
    private Lead[] leads = new Lead[100];

    public boolean add(Lead lead) {
        //Проверка на дубликаты
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null
                && leads[i].getEmail().equals(lead.getEmail())) {
                return false; //Такой лид уже есть
            }
        }

        //Поиск свободного места для добавления нового лида
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] == null) {
                leads[i] = lead;
                return true; //Лид добавлен
            }
        }

        //Информируем о том, что свободных ячеек нет
        throw new IllegalStateException("Storage is full, cannot add more leads");
    }

    public Lead[] findAll() {
        //Создаём переменную счётчик
        int count = 0;

        //Цикл для вычисления длины будущего массива
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                count++;
            }
        }

        //Создаём массив для вывода
        Lead[] result = new Lead[count];

        //Обнулям счётчик, чтобы использовать его снова
        count = 0;

        //Заполняем результирующий массив всеми непустыми элементами из начального
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                result[count++] = leads[i];
            }
        }

        //Возвращаем массив
        return result;
    }

    public int size() {
        //Создаём переменную счётчик
        int count = 0;

        //Цикл для вычисления количества заполненных ячеек
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] != null) {
                count++;
            }
        }

        //Возвращаем количество заполненных элементов
        return count;
    }

    static void main() {
        LeadStorage l1 = new LeadStorage();
        System.out.println(l1.add(new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW")));
        System.out.println(l1.size());
    }
}