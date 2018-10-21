class Square extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.type == "empty") {
      if (this.props.onClick) {
        return (
          <span className="square" onClick={this.props.onClick}/>
        );
      } else {
        return (
          <span className="square"/>
        )
      }
    } else {
      let img;
      if (this.props.type == "landscape") {
        img = "images/" + this.props.landscape + "-" + this.props.crowns + ".jpg";
      } else {
        img = "images/castle-" + this.props.color + ".jpg";
      }

      return (
        <img className="square" src={img}/>
      );
    }
  }
}

class Kingdom extends React.Component {
  render() {
    let row;
    let column;
    let squares = [];
    for (row = 0; row < this.props.squares.length; row++) {
      for (column = 0; column < this.props.squares[row].length; column++) {
        let component;
        const square = this.props.squares[row][column];
        if (square == null) {
          const isClickable = this.isEmptySquareClickable(row, column);
          // TODO: add onClick
          component =
            <Square
              key={row + ',' + column}
              type="empty"
              row={row}
              column={column}
            />;
        } else {
          if (square.landscape != null) {
            component =
              <Square
                key={row + ',' + column}
                type="landscape"
                landscape={square.landscape}
                crowns={squares.crowns}
              />;
          } else {
            component =
              <Square
                key={row + ',' + column}
                type="castle"
                color={this.props.playerColor}
              />;
          }
        }
        squares.push(component);
      }
    }
    // TODO: add shiftUp, shiftLeft, shiftDown, shiftRight
    return (
      <div className="kingdom">
        <div>{this.props.playerName}</div>
        <div className="squares-container">
          {squares}
        </div>
      </div>
    );
  }

  isEmptySquareClickable(row, column) {
    // TODO: check for adjacent to existing tile
    return this.props.isPlacing;
  }

  handleSquareClick(row, column) {
    console.log('Clicked on row=' + row + ', column=' + column);
  }
}

class Tile extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    let player = this.props.player;
    if (player == null) {
      player = "unclaimed";
    }

    // TODO: add onClick to tile if this.props.isSelecting and player == "unclaimed"
    return (
      <div className="tile">
        <Square
          type="landscape"
          landscape={this.props.squares[0].landscape}
          crowns={this.props.squares[0].crowns}
        />
        <Square
          type="landscape"
          landscape={this.props.squares[1].landscape}
          crowns={this.props.squares[1].crowns}
        />
        <div>{player}</div>
      </div>
    );
  }
}

class RoundTiles extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    // TODO: add isSelecting prop based on currentTurn
    const tiles = this.props.tiles.map((tile) =>
      <Tile
        key={tile.rank}
        squares={tile.squares}
        player={tile.player}
      />
    );

    return (
      <div className="stage">
        <div>{this.props.label}</div>
        <div className="tiles-container">
          {tiles}
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
      status = "Initializing...";
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
        <RoundTiles
          label="This round"
          tiles={this.state.thisRoundTiles}
        />
      );
    }
    let nextRoundContent = "";
    if (this.state.nextRoundTiles != null) {
      nextRoundContent = (
        <RoundTiles
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

ReactDOM.render(<Game />, document.getElementById("game"));