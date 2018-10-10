function Square(props) {
  return (
    <button className="square" onClick={props.onClick}>
      {props.value}
    </button>
  );
}

class Board extends React.Component {
  renderSquare(row, col) {
    return (
      <Square
        value={this.props.squares[row][col]]}
        onClick={() => this.props.onClick(row, col)}
      />
    );
  }

  renderRow(row) {
    return (
      <div className="board-row">
        {this.renderSquare(row, 0)}
        {this.renderSquare(row, 1)}
        {this.renderSquare(row, 2)}
        {this.renderSquare(row, 3)}
        {this.renderSquare(row, 4)}
      </div>
    );
  }

  render() {
    return (
      <div>
        {this.renderRow(0)}
        {this.renderRow(1)}
        {this.renderRow(2)}
        {this.renderRow(3)}
        {this.renderRow(4)}
      </div>
    );
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
    fetch("/gettabletop")
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

    const playerKingdom = currentPlayerKingdom();

    //squares[i] = this.state.xIsNext ? "X" : "O";
    //this.setState({
    //  history: history.concat([
    //    {
    //      squares: squares
    //    }
    //  ]),
    //  stepNumber: history.length,
    //  xIsNext: !this.state.xIsNext
    //});
  }

  currentPlayerKingdom()
  {
    const kingdoms = this.state.kingdoms;
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
    if (currentTurn.task == "GAME_OVER") {
      status = "Winner: " + currentTurn.player;
    } else {
      status = "Next player: " + currentTurn.player + " to " + currentTurn.task;
    }

    const playerKingdom = currentPlayerKingdom();

    return (
      <div className="game">
        <div className="game-board">
          <Board
            squares={playerKingdom.allSquares}
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

ReactDOM.render(<Game />, document.getElementById("root"));

