import java.util.ArrayList;

final public class BattleShip extends GameTool {

    private int size;
    public BattleShip (String i_species, String i_type, int i_size , char i_sign ,int i_score ){
        super(i_species , i_type , i_sign, i_score);
        size = i_size;
    }

    public int getShipSize() {
        return size;
    }


    /*return true if the ship distroyed*/
    @Override
    public boolean updateHitMe(Position position) {
        if (size == 0 ){
            System.out.print("ERROR: BattleShip.updateHitMe() -  size = 0 ");
            //TODO: THROW EXCEPTION
        }
        size--;
        positionsHited.add(position);
        if (size == 0){
            super.setAlive(false);
            return true;
        }
        return false;
    }



    @Override
    public int getScore() {
        return super.score;
    }
}
