public class SpellEffectHandler {

    GameHandler game;

    public SpellEffectHandler(GameHandler game) {
        this.game = game;
    }

    public void o_f(Cell targetCell) {
        targetCell.currentPiece.cellID = -1;
        targetCell.currentPiece = null;
        infernoEffect(1, targetCell);
        targetCell.updateIcon();
        game.window.updateText(false, false);
    }

    private void infernoEffect(int timer, Cell targetCell) {
        System.out.println("EffectedCellID:" + targetCell.id);
        targetCell.status = CellStatus.DEATH;
        targetCell.timer = timer;
    }

    private void removePiece() {

    }

}
