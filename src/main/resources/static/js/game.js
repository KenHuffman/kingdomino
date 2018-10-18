class Kingdom extends React.Component {
  renderSquare(row, col, playerColor) {
    const square=this.props.squares[row][col];

    let content;
    if (square == null) {
      content = (
        <span className="square">empty</span>
      );
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

      if (this.props.onClick) {
        return (
          <button onClick={this.props.onClick(row, col)}>
            {content}
          </button>
        );
      } else {
        return content;
      }
    }
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
}

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

    const content = this.state.kingdoms.map((kingdom) =>
      <Kingdom
        squares={kingdom.allSquares}
        playerName={kingdom.player.name}
        playerColor={kingdom.player.color}
        onClick={(row, col) => this.handleSquareClick(row, col)}
      />
    );

    // TODO display currentTurn from state
    return (
      <div className="game">
        <div className="game-board">
          {content}
        </div>
        <div className="game-info">
          <div>{status}</div>
        </div>
      </div>
    );
  }
}

// ========================================

ReactDOM.render(<Game />, document.getElementById("game"));

