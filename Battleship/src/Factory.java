import GameParser.BattleShipGame;
import GameParser.BattleShipGame.Boards.Board.Ship;
import GameParser.BattleShipGame.ShipTypes.ShipType;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Factory {
    BattleShipGame GameData;
    Validator GameDataValidator ;

    Factory(String xmlPath) throws Exception {
        Parser parsedGame = new Parser(xmlPath);
        GameData = parsedGame.GetParsedGame();
        GameDataValidator = new Validator(parsedGame.GetParsedGame().getBoardSize());
    }

    public Validator getGameDataValidator() {
        return GameDataValidator;
    }

    public Player[] createPlayers() throws Exception {
        //Validation pahse :
        try {
            GameDataValidator.ValidateShipTypes(GameData.getShipTypes().getShipType() , GameData.getBoards().getBoard());
        }
        catch (Exception e) {
            throw e ;
        }

        Player[] PlayersArray  = new Player[2];

        for(int player = 0 ; player < 2 ; player++) {
            GameTool[][] board = initPlayerBoard(GameData.getBoards().getBoard().get(player).getShip() , player + 1);
            PlayersArray[player] = new Player("Player" + (player + 1), GameData.getBoardSize(), board, GameData.getBoards().getBoard().get(player).getShip().size());

        }
        return PlayersArray;
    }

    private GameTool[][] initPlayerBoard(List<Ship> ships , int player) throws Exception {
        int size = GameData.getBoardSize();
        int column = -1 , row = -1 ;
        GameTool[][] board = new GameTool[size][size];

        try {
            amountOfShips();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        try {
            shipsScorePositive();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        try {
            shipsLengthPositive();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        try{
            ducplecateShipType();
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }

        try {
            amountOfShipsMatch(); }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        for(Ship ship : ships) {

            row = ship.getPosition().getX();
            column = ship.getPosition().getY();
            try {
                setBoard(column, row, board, ship.getDirection(), ship.getShipTypeId());
            } catch (Exception e) {
                throw e ;
            }
        }
        return board;
    }

    private void amountOfShips() throws Exception {
        int boardNum = 1 ;
        for(BattleShipGame.Boards.Board b: GameData.getBoards().getBoard()) {
            if(b.getShip().size() < 1) {
                throw new Exception("On Player" + boardNum + " board there are no ships! must be minimum 1 ship to play.");
            }
            boardNum++;
        }
    }

    private void shipsScorePositive() throws Exception {
        for(ShipType type : GameData.getShipTypes().getShipType()) {
            if(type.getScore() < 0) {
                throw new Exception(type.getId() + " score is " + type.getScore() +". score has to be >= 0.");
            }
        }
    }

    private void shipsLengthPositive() throws Exception {
        for(ShipType type : GameData.getShipTypes().getShipType()) {
            if(type.getLength() <= 0) {
                throw new Exception(type.getId() + " Length is " + type.getLength() +". Length has to be > 0.");
            }
        }
    }

    private void ducplecateShipType() throws Exception {
        for(ShipType type : GameData.getShipTypes().getShipType()){
            if(GameData.getShipTypes()
                    .getShipType()
                    .stream()
                    .filter(t -> t.getId().equals(type.getId()))
                    .count() > 1) {
                throw new Exception(type.getId() + " appears " + GameData.getShipTypes()
                        .getShipType()
                        .stream()
                        .filter(t -> t.getId().equals(type.getId()))
                        .count() + " Times . Type has to be unique!" );
            }
        }
    }

    private boolean amountOfShipsMatch() throws Exception {
        int boardNum = 1;
        for(BattleShipGame.Boards.Board board : GameData.getBoards().getBoard()) {
            for (ShipType type : GameData.getShipTypes().getShipType()) {
                if(type.getAmount() != countShipByType(board ,type.getId())) {
                    throw new Exception("You decleared Amount of " + type.getId() + " to be " +
                            type.getAmount() +" but there are " +
                            countShipByType(board,type.getId()) +
                            " on board number " + boardNum + ".\n\rThey have to be match!");
                }
            }
            boardNum++;
        }
        return true;
    }

    private int countShipByType(BattleShipGame.Boards.Board board, String id) {

        return (int)board.getShip().stream().filter(s -> s.getShipTypeId().equals(id)).count();

    }

    private void setBoard(int column, int row, GameTool[][] board, String shipDirection, String shipTypeId) throws Exception {
        BattleShip bship = new BattleShip("Ship" ,shipTypeId ,getShipSizeByType(shipTypeId) , '#' , getScoreByShipTypeId(shipTypeId) , shipDirection);
        int numberOfIterations = 0;
        int tempCol = column ;
        int tempRow = row ;
        boolean isFormatSupported = false ;
        bship.setCoordinates(row,column);


        if(!GameDataValidator.canGameToolBePlaced(bship , board)) {
            throw new Exception("Problem placing : " + bship.getType() + " in board . \n row : " + bship.getRow() + " column : " + bship.getColumn() + " \n");
        }


        if(shipDirection.equals("ROW")) {
            for (; numberOfIterations < bship.getSize(); numberOfIterations++, tempCol++) {
                board[tempRow][tempCol] = bship;
            }
            isFormatSupported = true ;
        }

        if(shipDirection.equals("COLUMN")) {
            for( ; numberOfIterations < bship.getSize() ; numberOfIterations++ , tempRow++) {
                board[tempRow][tempCol] = bship;
            }
            isFormatSupported = true;
        }

        if(!isFormatSupported){throw new Exception(shipDirection + " direction is not supported!");}


    }

    public int getScoreByShipTypeId(String id) {
        List<ShipType> types = GameData.getShipTypes().getShipType();
        int score =0 ;
        for(ShipType type : types) {
            if(id.equals(type.getId())) {
                score = type.getScore();
            }
        }
        return score;
    }

    private void printb(GameTool[][] board) {
        for(int i = 0 ; i < GameData.getBoardSize() ; i++) {
            for (int j = 0 ; j < GameData.getBoardSize(); j++) {
                if(board[i][j] == null) {
                System.out.printf("0");
                } else {
                    System.out.printf("*");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private int getShipSizeByType(String id) {
        List<ShipType> types = GameData.getShipTypes().getShipType();
        int length = 0 ;
        for(ShipType type : types) {
            if(id.equals(type.getId())) {
                length = type.getLength();
            }
        }
        return length;

    }
}
