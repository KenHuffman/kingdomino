class Kingdom extends React.Component {
  renderPlotSquare(row, col, playerColor) {
    const square=this.props.squares[row][col];

    let content;
    if (square == null) {
      if (this.isEmptySquareClickable(row, col)) {
        content = (
          <span className="square" onClick={() => this.handleSquareClick(row, col)}/>
        );
      } else {
        content = (
          <span className="square"/>
        );
      }
    } else {
      let img;
      if (square.landscape == null) {
        img = "images/castle-" + playerColor + ".jpg";
      } else {
        img = "images/" + square.landscape + "-" + square.crowns + ".jpg";
      }
      content = (
        <img className="square" src={img}/>
      );
    }

    return content;
  }

  renderRow(row, playerColor) {
    // TODO: how to loop?
    return (
      <div className="board-row">
        {this.renderPlotSquare(row, 0, playerColor)}
        {this.renderPlotSquare(row, 1, playerColor)}
        {this.renderPlotSquare(row, 2, playerColor)}
        {this.renderPlotSquare(row, 3, playerColor)}
        {this.renderPlotSquare(row, 4, playerColor)}
      </div>
    );
  }

  render() {
    // TODO: how to loop?
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

class StagedTiles extends React.Component {
  constructor(props) {
    super(props);
  }

  renderTileSquare(square) {
    let img = "images/" + square.landscape + "-" + square.crowns + ".jpg";
    let content;

    content = (
      <img className="square" src={img}/>
    );

    return content;
  }

  renderTile(slot, isSelecting) {
    const tile = slot.tile;
    let player = slot.player;

    let clickable = false;
    if (player == null) {
      player = "available";
      clickable = isSelecting;
    }

    // TODO: add onClick to player if is clickable
    return (
      <div className="tile">
        <div>
          {this.renderTileSquare(tile.squares[0])}
          {this.renderTileSquare(tile.squares[1])}
        </div>
        <div>{player}</div>
      </div>
    );
  }

  render() {
    // TODO: how to loop?
    let isSelecting = false;
    return (
      <div className="stage">
        <div>{this.props.label}</div>
        <div>
          {this.renderTile(this.props.tiles[0], isSelecting)}
          {this.renderTile(this.props.tiles[1], isSelecting)}
          {this.renderTile(this.props.tiles[2], isSelecting)}
          {this.renderTile(this.props.tiles[3], isSelecting)}
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
      status = "Initialing...";
    }

    const kingdomContent = this.state.kingdoms.map((kingdom) =>
      <Kingdom
        key={kingdom.player.name}
        playerName={kingdom.player.name}
        playerColor={kingdom.player.colorName}
        isPlacing={currentTurn != null && currentTurn.player.name == kingdom.player.name && currentTurn.task == "PLACING_TILE"}
        squares={kingdom.allSquares}
        onClick={(row, col) => this.handleSquareClick(row, col)}
      />
    );

    let thisRoundContent = "";
    if (this.state.thisRoundTiles != null) {
      thisRoundContent = (
        <StagedTiles
          label="This round"
          tiles={this.state.thisRoundTiles}
        />
      );
    }
    let nextRoundContent = "";
    if (this.state.nextRoundTiles != null) {
      nextRoundContent = (
        <StagedTiles
          label="Next round"
          tiles={this.state.nextRoundTiles}
        />
      );
    }

    return (
      <div>
        <div>
          {kingdomContent}
        </div>
        {thisRoundContent}
        {nextRoundContent}
        <div>
          {status}
        </div>
      </div>
    );
  }
}

// ========================================

ReactDOM.render(<Game />, document.getElementById("game"));

