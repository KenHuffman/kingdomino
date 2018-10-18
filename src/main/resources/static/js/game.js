class Kingdom extends React.Component {
  renderSquare(row, col, playerColor) {
    const square=this.props.squares[row][col];

    let content;
    if (square == null) {
      if (this.isEmptySquareClickable(row, col)) {
        content = (
          <span className="square" onClick={() => this.handleSquareClick(row, col)}>empty</span>
        );
      } else {
        content = (
          <span className="square">empty</span>
        );
      }
    } else {
      let img;
      if (square.landscape == null) {
        img = "castle-" + playerColor + ".jpg";
      } else {
        img = square.landscape + "-" + square.crowns + ".jpg";
      }
      // TODO: <img src={img}/>
      content = (
        <span className="square">{img}</span>
      );
    }

    return content;
  }

  renderRow(row, playerColor) {
    return (
      <div className="board-row">
        {this.renderSquare(row, 0, playerColor)}
        {this.renderSquare(row, 1, playerColor)}
        {this.renderSquare(row, 2, playerColor)}
        {this.renderSquare(row, 3, playerColor)}
        {this.renderSquare(row, 4, playerColor)}
      </div>
    );
  }

  render() {
    return (
      <div>
        <div>{this.props.playerName}</div>
        <div>
          {this.renderRow(0, this.props.playerColor)}
          {this.renderRow(1, this.props.playerColor)}
          {this.renderRow(2, this.props.playerColor)}
          {this.renderRow(3, this.props.playerColor)}
          {this.renderRow(4, this.props.playerColor)}
        </div>
      </div>
    );
  }

  isEmptySquareClickable(row, col) {
    // TODO: check for adjacent to existing tile
    return this.props.isPlacing;
  }

  handleSquareClick(row, col) {
    console.log('Clicked on row=' + row + ', col=' + col);
  }
}

// TODO: class Tile extends React.Component {
// }

class Game extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdoms: []
    };
  }

  componentDidMount() {
    fetch("getgame")
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            kingdoms: result.kingdoms,
            thisRoundTiles: result.thisRoundTiles,
            nextRoundTiles: result.nextRoundTiles,
            currentTurn: result.currentTurn
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            error
          });
        }
      );
  }

  handleSquareClick(row, col) {
    const playerKingdom = this.currentPlayerKingdom();
  }

  currentPlayerKingdom()
  {
    const kingdoms = this.state.kingdoms;
    if (kingdoms == null) {
      return null;
    }

    for (let i = 0; i < kingdoms.length; i++) {
      const kingdom = kingdoms[i];
      if (kingdom.player.name == this.state.currentTurn.player.name) {
        return kingdom;
      }
    }
  }

  render() {
    const kingdoms = this.state.kingdoms;
    const currentTurn = this.state.currentTurn;

    let status;
    if (currentTurn != null) {
      if (currentTurn.task == "GAME_OVER") {
        status = "Winner: " + currentTurn.player.name;
      } else {
        status = "Next player: " + currentTurn.player.name + " to " + currentTurn.task;
      }
    } else {
      status = "No state.currentTurn";
    }

    const content = this.state.kingdoms.map((kingdom) =>
      <Kingdom
        key={kingdom.player.name}
        playerName={kingdom.player.name}
        playerColor={kingdom.player.colorName}
        isPlacing={currentTurn != null && currentTurn.player.name == kingdom.player.name && currentTurn.task == "PLACING_TILE"}
        squares={kingdom.allSquares}
        onClick={(row, col) => this.handleSquareClick(row, col)}
      />
    );

    return (
      <div className="game">
        <div>
          {content}
        </div>
        <div>
          {status}
        </div>
      </div>
    );
  }
}

// ========================================

ReactDOM.render(<Game />, document.getElementById("game"));

