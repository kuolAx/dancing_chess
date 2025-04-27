package chess.dancing.figures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseFigure {

    BaseFigure dancePartner;

    public abstract void move();

    public abstract void releaseCurrentDancePartner();
}
