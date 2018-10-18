function Square(props) {
  let img;
  if (props.square == null) {
    return (<span>empty</span>);
  }
  if (props.square.landscape == null) {
    // get color of castle
    img = "castle-" + props.playerColorName + ".jpg"
  } else {
    img = props.square.landscape + ".jpg"
  }
  //<button className="square" onClick={props.onClick}>{img}</button>
  return (
    <img src={img}/>
  );
}

class Kingdom extends React.Component {
  renderSquare(row, col, playerColorName) {
    return (
      <Square
        square={this.props.squares[row][col]}
        playerColorName={playerColorName}
        onClick={() => this.props.onClick(row, col)}
      />
    );
  }

  renderRow(row, playerColorName) {
    return (
      <div className="board-row">
        {this.renderSquare(row, 0, playerColorName)}
        {this.renderSquare(row, 1, playerColorName)}
        {this.renderSquare(row, 2, playerColorName)}
        {this.renderSquare(row, 3, playerColorName)}
        {this.renderSquare(row, 4, playerColorName)}
      </div>
    );
  }

  render() {
    if (this.props.squares) {
      // TODO display player name from props
      return (
        <div>
          {this.renderRow(0, this.props.playerColorName)}
          {this.renderRow(1, this.props.playerColorName)}
          {this.renderRow(2, this.props.playerColorName)}
          {this.renderRow(3, this.props.playerColorName)}
          {this.renderRow(4, this.props.playerColorName)}
        </div>
      );
    } else {
      return (
        <div>No board!</div>
      );
    }
  }
}

class Game extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdoms: [],
      stagedTiles: []
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
    const task = this.state.currentTurn.task;
    if (task == "GAME_OVER") {
      return;
    }

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

    const playerKingdom = this.currentPlayerKingdom();
    let gameSquares;
    let playerColorName;
    if (playerKingdom != null) {
      gameSquares = playerKingdom.allSquares;
      playerColorName = playerKingdom.player.colorName;
    } else {
      gameSquares = null;
      playerColorName = null;
    }

    // TODO display currentTurn from state
    return (
      <div className="game">
        <div className="game-board">
          <Kingdom
            squares={gameSquares}
            playerColorName={playerColorName}
            onClick={(row, col) => this.handleSquareClick(row, col)}
          />
        </div>
        <div className="game-info">
          <div>{status}</div>
          <ol>curly brace stuff</ol>
        </div>
      </div>
    );
  }
}

// ========================================

ReactDOM.render(<Game />, document.getElementById("game"));

