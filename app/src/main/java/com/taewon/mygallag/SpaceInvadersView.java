package com.taewon.mygallag;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.taewon.mygallag.sprites.AlienSprite;
import com.taewon.mygallag.sprites.Sprite;
import com.taewon.mygallag.sprites.StarshipSprite;

import java.util.ArrayList;
import java.util.Random;

public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    
    // 1. SurfaceView 는 새로운 Thread 를 이용해 화면을 Rendering 시키는 방법 -> View 보다 빠름(애니메이션, 영상 처리에 이용)
    // 2. Android 에서 새로운 Thread 에서 작업을 실행시키는 방법 : (Thread.class, Runnable.interface)
    // 3. Thread 를 상속하면 SurfaceView 를 상속할 수 없기 때문에(자바의 일반 Class 는 부모 Class 를 하나만 가져야 함) Runnable Interface 를 상속

    private static final int MAX_ENEMY_COUNT = 10;      // 적 최대 개체수
    private Context context;
    private int characterId;            // 캐릭터 이미지 ID
    private SurfaceHolder ourHolder;    // 'Surface'를 관리하는 'Holder'를 생성 --> SurfaceHolder 를 상속했기 때문에 사용가능
    private Paint paint;                // 그래픽을 그리기위한 변수
    public int screenW, screenH;        // 게임화면의 끝 좌표
//    private Rect src;
    private Rect dst; // 게임화면 크기만큼의 사각형 그리기 위한 변수
    private ArrayList sprites = new ArrayList();            // 개체들을 담는 배열(ArrayList)
    private Sprite starship;
    private int score, currEnemyCount;

    private Thread gameThread = null;       // View 를 Rendering 할 Thread 변수
    private volatile boolean running;
    /*
     1. volatile : Main Memory 에 read, write 를 보장하는 키워드
        이 변수를 Main Memory 에만 저장 (volatile 을 사용 하지 않는 변수는 CPU cache 에 저장)
     2. Main Memory 와 CPU cache 의 값이 다른 경우 사용
     3. 주의점 : 성능에 어느 정도 영향을 줌

        하나의 Thread 가 Write, 나머지 Thread 가 Read 인 경우
        또는
        변수의 값이 최신의 값으로 읽어와야 하는 경우
        일 때 사용

     Sol) Multi Thread 환경에서 Main Memory 값을 참조하므로 변수 불일치를 해결함
     */

    private Canvas canvas;              // 그래픽을 그리기 위한 변수
    int mapBitmapY = 0;                 // 배경 맵을 스크롤하는 변수

    // MainActivity -> init() 메소드에서 구현
    public SpaceInvadersView (Context context, int characterId, int x, int y) {
        super(context);
        this.context = context;
        this.characterId = characterId;      // 내 캐릭터 이미지 Id
        ourHolder = getHolder();            // SurfaceView return
        paint = new Paint();
        screenW = x;                        // View 의 가로 크기 return
        screenH = y;                        // View 의 세로 크기 return
/*src = new Rect(); // 원복 */    // 얘 뭐임???????????
        dst = new Rect();                           // View 의 크기의 사각형
        dst.set(0, 0, screenW, screenH);   // (0, 0), (가로 크기, 세로 크기)
        startGame();        // 게임 시작을 위한 메소드
    }
    
    
    private void startGame() {      // 생성자, SurfaceCreate 에서 사용, 따라서 초기화, 게임 실행 하는 메소드가 들어감
        sprites.clear();                    // 개체 배열 초기화
        initSprites();                      // 개체 배열에 추가 시키는 메소드
        // View 가 Rendering 될 때 Sprite 도 그려줌
        score = 0;
    }

    private void initSprites() {
        starship = new StarshipSprite(context, this, characterId, screenW / 2, screenH - 400,1.5f);
        // StarshipSprite(Context, Game, 내 캐릭터 이미지 Id, X 위치, Y 위치, 속도)
        sprites.add(starship); // 개체 배열에 내 캐릭터 추가
        spawnEnemy();       // 개체 배열에 적 캐릭터 추가하는 메소드
        spawnEnemy();
    }
    public void spawnEnemy() {
        Random r = new Random();
        int x = r.nextInt(300) + 100; // 100 ~ 399
        int y = r.nextInt(300) + 100;
        
        Sprite alien = new AlienSprite(context, this, R.drawable.ship_0002, 100 + x, 100 + y);
        // AlienSprite(Context, Game, 적 캐릭터 이미지, X 위치, Y 위치)
        sprites.add(alien);                 // 개체 배열에 추가
        currEnemyCount++;                   // 현재 적 개체수 증가
    }
    
    // Runnable 의 메소드 Override
    @Override
    public void run() {     // Thread 가 동작 중 이라면 계속 실행
        while (running) {
            Random r = new Random();
            boolean isEnemySpawn = r.nextInt(100) + 1 < (getPlayer().speed + (int) (getPlayer().getPowerLevel() / 2));
            // 랜덤 정수(1 ~ 100) < (내 캐릭터 속도) + (내 캐릭터의 PowerLevel) / 2
            // 위 조건이 참이면 true, 거짓이면 false
            
            if (isEnemySpawn && currEnemyCount < MAX_ENEMY_COUNT) spawnEnemy();
            // isEnemySpawn, (현재 적 개체수 < 최대 적 개체수)
            // 위 조건이 모두 만족하면 적 개체 추가
            
            for (int i = 0; i < sprites.size(); i++) {      // 0 부터 개체 배열의 크기만큼 반복
                Sprite sprite = (Sprite) sprites.get(i);    // 개체를 하나씩 가져와서 sprite 변수에 추가
                sprite.move();                              // 가져온 개체 Move 메소드 실행
            }

            for (int p = 0; p < sprites.size(); p ++) {     // List 의 요소들을 한 번씩 반복
                for (int s = p + 1; s < sprites.size(); s++) {  // 위 요소들의 다음 인덱스 부터 한 번씩 반복
                    try {
                        Sprite me = (Sprite) sprites.get(p);            // 바깥 For Index 의 개체
                        Sprite other = (Sprite) sprites.get(s);         // 안쪽 For Index 의 개체

                        if (me.checkCollision(other)) {     // 'me' 와 'other' 의 충돌 체크
                            // checkCollision : 두 개의 Sprite 가 충돌 여부를 판단하는 로직을 포함
                            me.handleCollision(other);      // 메소드 호출하여 충돌 처리
                            other.handleCollision(me);
                        }
                    } catch (Exception e) {     // 예외가 발생해도 코드를 중단되지 않도록 처리
                        e.printStackTrace();
                    }
                }
            }
            draw();

            // 게임의 프레임 간의 간격을 조절하기 위해 사용
            // 일정시간 만큼의 딜레이를 줘서 프로그램의 실행 속도 제어하기 위함
            try {
                Thread.sleep(10);       // 현재 실행중인 Thread 를 10 Millisecond 만큼 멈춤 (0.01 초)
            } catch (Exception e) {

            }
        }
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {         // 그래픽을 그리기 위한 Surface 가 유효한지 확인
            canvas = ourHolder.lockCanvas();            // Canvas 객체 가져오기
            canvas.drawColor(Color.BLACK);              // Canvas 의 배경색을 검정색으로

            mapBitmapY++;       // 배경 맵을 스크롤하는 데 사용하는 변수 증가

            if (mapBitmapY < 0) mapBitmapY = 0;     // 음수이면 0으로

            paint.setColor(Color.BLUE);             // 그리기에 사용되는 색을 파란색으로

            for (int i = 0; i < sprites.size(); i++) {  // 개체를 담은 배열의 크기만큼 반복함
                Sprite sprite = (Sprite) sprites.get(i);
                sprite.draw(canvas, paint);             // Canvas 에 해당 Sprite 그려줌
            }

            ourHolder.unlockCanvasAndPost(canvas);      // Canvas 를 잠금 해제하고, 변경 된 그래픽을 Rendering
        }
    }

    public void endGame() {
//        Log.e("GameOver", "GameOver");
        Intent intent = new Intent(context, ResultActivity.class);            // ResultActivity 를 담는 Intent 변수
        intent.putExtra("score", score);    // Extra 에 Score 추가
        context.startActivity(intent);            // Activity 이동
        gameThread.stop();                        // View 를 Rendering 하고 있는 Thread 종료
    }
    public void resume() {      // 게임이 실행 중일 때 동작
        running = true;     // 실행 여부를 받는 휘발성 변수
        gameThread = new Thread(this);  // this : Runnable Interface
        gameThread.start(); // Thread 실행
    }
    public void pause() {       // 게임이 중지 될 때 동작
        running = false;        // 실행 여부 False, Run 메소드가 반복 실행 되는 것을 멈춤
        try {
            gameThread.join(); // 현재 실행중인 gameThread 가 완료될 때 까지 대기
        } catch (InterruptedException e) {

        }
    }
    
    public void removeSprite(Sprite sprite) {       // 개체 배열에서 개체 삭제하는 메소드  
        sprites.remove(sprite);
    }

    /*
        개체 배열 Getter
        내 캐릭터 가져오는 메소드
        Score : Setter, Getter
        현재 적개체 수 : Setter, Getter
     */
    public ArrayList getSprites() {
        return sprites;
    }
    public StarshipSprite getPlayer() {
        return (StarshipSprite) starship;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCurrEnemyCount(int currEnemyCount) {
        this.currEnemyCount = currEnemyCount;
    }

    public int getCurrEnemyCount() {
        return currEnemyCount;
    }

    // SurfaceHolder.Callback Interface 를 상속받아 Override
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {         // Surface 가 생성될 때 호출
        startGame();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }   // Surface 가 변경 될 때 호출

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }       // Surface 가 소멸될 때 호출

}
