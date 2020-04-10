import com.google.gson.Gson;

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

//1. создать функцию, которая получает на вход запрос (перенести в нее код запроса)
//2. дополнить Request и Responce необходимыми полями, чтобы они могли использоваться для любых запросов
// некоторые поля могут и не использоваться (например токен в реквест)

//клиент серверное приложение будет делать как минимум три действия: регистрация, получение списка карт, запрос на сет,
//поэтому каждый раз будет запрос к серверу, следовательно нужно сделать этот запрос отдельной функцией
//надо будет переписать конструкторы у Request и Responce, потому что добавятся поля
//возможно потребуется несколько конструкторов для регистрации


class Request{
    String action, nickname;
    int token;
    //ArrayList<Card>cards;
    Card[] set;

    //для регистрации пользователя       в action "register"
    public Request(String action, String nickname) {
        this.action = action;
        this.nickname = nickname;
    }

    //для получения карт      в action "fetch_cards" и токен без ковычек
    public Request(String action, int token){
        this.action = action;
        this.token = token;
    }

    //для получения сета      в action "take_set" и токен без ковычек
    public Request(String action, int token, Card[] set){
        this.action = action;
        this.token = token;
        this.set = set;
    }

}
//здесь конструктор не нужен, потому что gson сам вручную запишет значения
class Responce{
    //результат регистрации
    String status;
    int token;

    //результат получения карт (тут же статус)
    ArrayList<Card>cards;

    //результат получения сета
    int points, countCard;

}

public class Main {

    //функция, которая выполянет запрос (передается параметр Request, отдает Responce)
    public static Responce serverRequest(Request reg) throws IOException{
        //обработать исключения
        //4 места где возникают исключения (не запихивать в один try catch и не делать для каждого try catch)
        String set_server_url = "http://194.176.114.21:8057";
        Gson gson = new Gson();
        URL url = new URL(set_server_url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true); //запись, т.е. отправка чего-то на сервер
        //String data = "{\"action\": \"register\", \"nickname\": \"Olga\"}";
        //Request req = new Request("register", "Olga1"); //формирование запроса
        Request request = reg;
        OutputStream out = urlConnection.getOutputStream();
        //out.write(data.getBytes());
        out.write(gson.toJson(request).getBytes()); //через gson запрос сериализуется в байты
        InputStream stream = urlConnection.getInputStream();
        Responce responce = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), Responce.class);
        return responce;
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Никнейм:");
        Scanner sc = new Scanner(System.in);
        String nickname = sc.nextLine();

        //регистрация на сервере
        Request req1 = new Request("register", nickname);
        Responce res1 = serverRequest(req1);
        System.out.println(res1.token);

        boolean isEmpy = false;
        while (isEmpy == false) {
        //получение списака карт с сервера
        Request req2 = new Request("fetch_cards", res1.token);
        Responce res2 = serverRequest(req2);
        System.out.println(res2.status);
        //поиск сета
            ArrayList<Card> cards = res2.cards;
            System.out.println(cards.toString());
            Card [] set = new Card[3];
            Card card3 = set[0];
            for (Card c: cards) {
                for (Card c1:cards) {
                    if(!c.equals(c1)){
                        card3 = c.getThird(c1);
                        set[0] = c;
                        set[1] = c1;}
                }
            }

            for (Card c: cards) {
                if(c.equals(card3)){
                    set[2] = c;
                    System.out.println("Сет " + set[0].toString() + " и " + set[1].toString() + set[2].toString());
                }

            }
            //отправка сета
            Request req3 = new Request("take_set", res1.token, set);
            Responce res3 = serverRequest(req3);

            //System.out.println("Очки" + res3.points);
            //System.out.println("Кол-во осталось" +res3.countCard);
            if((res3.countCard) == 0){isEmpy = true;}
            isEmpy = false;
            }
        //Scanner sc = new Scanner(stream);
        //System.out.println(sc.nextLine());
        //System.out.println(responce.token);
    }
}


class Card {
    int fill, count, shape, color;

    public Card(int fill, int count, int shape, int color) {
        this.fill = fill;
        this.count = count;
        this.shape = shape;
        this.color = color;
    }


    public int getFill() {
        return fill;
    }

    public int getCount() {
        return count;
    }

    public int getShape() {
        return shape;
    }

    public int getColor() {
        return color;
    }


    public void setFill(int fill) {
        this.fill = fill;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public void setColor(int color) {
        this.color = color;
    }


    @Override
    public boolean equals(Object o) {
        Card card = (Card) o;
        return (card.fill == this.fill) && (card.count == this.count) && (card.shape == this.shape) && (card.color == this.color);
    }

    public String toString () {
        return "Карта: " + fill + " " + count + " " + shape + " " + color;
    }



    public Card getThird (Card c){
        Card card3 = new Card(fill,count,shape,color);
        //if ((this.color != c.color) && (this.shape == c.shape) && (this.shape == 1) && (this.count == c.count) && ((this.count == c.count == 1))){


        if (this.fill != c.fill){
            if ((this.fill == 1 && c.fill == 3) | (this.fill == 3 && c.fill == 1)){
                card3.fill = 2;
            }
            else {
                if ((this.fill == 1 && c.fill == 2) | (this.fill == 2 && c.fill == 1)) {
                    card3.fill = 3;
                } else {
                    card3.fill = 1;
                }
            }

        }
        else{
            card3.fill = this.fill;

        }


        if (this.count != c.count){
            if ((this.count == 1 && c.count == 3) | (this.count == 3 && c.count == 1)){
                card3.count = 2;
            }
            else {
                if ((this.count == 1 && c.count == 2) | (this.count == 2 && c.count == 1)) {
                    card3.count = 3;
                } else {
                    card3.count = 1;
                }
            }

        }
        else{
            card3.count = this.count;

        }
        if (this.shape != c.shape){
            if ((this.shape == 1 && c.shape == 3) | (this.shape == 3 && c.shape == 1)){
                card3.shape = 2;
            }
            else {
                if ((this.shape == 1 && c.shape == 2) | (this.shape == 2 && c.shape == 1)) {
                    card3.shape = 3;
                } else {
                    card3.shape = 1;
                }
            }

        }
        else{
            card3.shape = this.shape;

        }


        if (this.color != c.color){
            if ((this.color == 1 && c.color == 3) | (this.color == 3 && c.color == 1)){
                card3.color = 2;
            }
            else {
                if ((this.color == 1 && c.color == 2) | (this.color == 2 && c.color == 1)) {
                    card3.color = 3;
                } else {
                    card3.color = 1;
                }
            }

        }
        else{
            card3.color = this.color;

        }
        return card3;
    }
}
