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
          // TODO: add onClick to handleSquareClick(row, column);
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
    let label = this.props.playerName;
    if (this.props.currentPlayer) {
      label += ", should place the tile";
    }

    // TODO: add shiftUp, shiftLeft, shiftDown, shiftRight
    return (
      <div className="kingdom">
        <div>{label}</div>
        <div className="squares-container">
          {squares}
        </div>
      </div>
    );
  }

  isEmptySquareClickable(row, column) {
    // TODO: check for adjacent to existing tile or adjacent to first click
    return this.props.isPlacing;
  }
}

class Tile extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    let ownerContent = "";
    if (this.props.tileOwner == null) {
      if (this.props.currentPlayer) {
        ownerContent =
          <button onClick={() => this.props.onTileSelection(this.props.currentPlayer, this.props.rank)}>
            {this.props.currentPlayer.name}
          </button>;
      } else {
        ownerContent = <span>unclaimed</span>;
      }
    }
    else {
      ownerContent = <span>{this.props.tileOwner.name}</span>;
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
        <div>{ownerContent}</div>
      </div>
    );
  }
}

class RoundTiles extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    let label = this.props.label;
    if (this.props.currentPlayer) {
      label += ", " + this.props.currentPlayer.name + " should select a tile"
    }

    const tiles = this.props.tiles.map((tile) =>
      <Tile
        key={tile.rank}
        rank={tile.rank}
        squares={tile.squares}
        tileOwner={tile.owner}
        currentPlayer={this.props.currentPlayer}
        onTileSelection={this.props.onTileSelection}
      />
    );

    return (
      <div className="stage">
        <div>{label}</div>
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
    this.handleTileSelection = this.handleTileSelection.bind(this);
  }

  refreshGame(result)
  {
    this.setState({
      kingdoms: result.kingdoms,
      thisRoundTiles: result.thisRoundTiles,
      nextRoundTiles: result.nextRoundTiles,
      currentTurn: result.currentTurn
    });
  }

  componentDidMount() {
    fetch("getgame")
      .then(res => res.json())
      .then(
        (result) => {
          this.refreshGame(result);
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

  handleTileSelection(currentPlayer, rank) {
    fetch("claimtile/" + encodeURIComponent(currentPlayer.name) + "/" + rank, {
      method: 'PUT'
    })
    .then(res => res.json())
    .then(
      (result) => {
        this.refreshGame(result);
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
  }

  render() {
    const kingdoms = this.state.kingdoms;
    const currentTurn = this.state.currentTurn;

    let status;
    let thisRoundSelector = null;
    let nextRoundSelector = null;
    if (currentTurn != null) {
      if (currentTurn.task == "GAME_OVER") {
        status = "Winner: " + currentTurn.player.name;
      } else {
        status = "Next player: " + currentTurn.player.name + " to " + currentTurn.task;
        if (currentTurn.task = "CHOOSING_INITIAL_TILE") {
          thisRoundSelector = currentTurn.player;
        } else if (currentTurn.task = "CHOOSING_NEXT_TILE") {
          nextRoundSelector = currentTurn.player;
        }
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
          currentPlayer={thisRoundSelector}
          onTileSelection={this.handleTileSelection}
        />
      );
    }
    let nextRoundContent = "";
    if (this.state.nextRoundTiles != null) {
      nextRoundContent = (
        <RoundTiles
          label="Next round"
          tiles={this.state.nextRoundTiles}
          currentPlayer={nextRoundSelector}
          onTileSelection={this.handleTileSelection}
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