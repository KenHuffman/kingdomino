//import React from 'react';
//import ReactDOM from 'react-dom';
//import Button from '@material-ui/core/Button';

class Hint extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div
        className={'hint dir-' + this.props.direction}
        onMouseEnter={() => this.props.onMouseEnter(this.props.direction)}
        onMouseLeave={() => this.props.onMouseLeave(this.props.direction)}
        onClick={() => this.props.onClick(this.props.direction)}
      />
    );
  }
}

class Square extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hintOverlay: null
    };
    this.handleClick = this.handleClick.bind(this);
    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.setHintOverlay = this.setHintOverlay.bind(this);
  }

  handleClick() {
    this.props.onLandscapeAction(this.props.row, this.props.column, direction, 'select');
  }

  handleMouseMove(evt) {
    if (this.props.placeable) {
      let direction;
      const doc = document.documentElement;
      const windowLeft = (window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0);
      const windowTop = (window.pageYOffset || doc.scrollTop)  - (doc.clientTop || 0);
      const posX = evt.clientX + windowLeft - evt.target.offsetLeft;
      const posY = evt.clientY + windowTop - evt.target.offsetTop;
      if (posX > posY) {
        if (posY > evt.target.clientWidth - posX) {
          direction = "west";
        } else {
          direction = "north";
        }
      } else {
        if (posY > evt.target.clientWidth - posX) {
          direction = "south";
        } else {
          direction = "east";
        }
      }

      const placeable = this.props.placeable[direction];
      console.log('eventXY=(' + evt.clientX + ',' + evt.clientY +
        '),offsetXY=(' + evt.target.offsetLeft + ',' + evt.target.offsetTop +
        '), windowXY=(' + windowLeft + ',' + windowTop +
        '), posX=' + posX + ',posY=' + posY +
        ', r=' + this.props.row + ',c=' + this.props.column +
        ', direction=' + direction + ', placeable=' + placeable);
    }
    //this.props.onLandscapeAction(this.props.row, this.props.column, direction, 'clear');
    //this.props.onLandscapeAction(this.props.row, this.props.column, direction, 'hint');
  }

  setHintOverlay(landscapeSquare) {
    this.setState({
      hintOverlay: landscapeSquare
    });
  }

  render() {
    let landscape;
    let crowns;
    let castle;
    if (this.props.type == "landscape") {
      landscape = this.props.landscape;
      crowns = this.props.crowns;
    } else if (this.props.type == "castle") {
      castle = this.props.color;
    } else if (this.state.hintOverlay != null) {
      landscape = this.state.hintOverlay.landscape;
      crowns = this.state.hintOverlay.crowns;
    }

    let img = "";
    if (landscape) {
      img =
        <img
          className="landscape"
          src={"images/" + landscape + "-" + crowns + ".jpg"}
        />;
    } else if (castle) {
      img =
        <img
          className="landscape"
          src={"images/castle-" + castle + ".jpg"}
        />;
    }

    return (
      <div className="square"
        onMouseMove={(e) => this.handleMouseMove(e)}>
        {img}
      </div>
    );
  }
}

class Kingdom extends React.Component {
  constructor(props) {
    super(props);
    this.handleLandscapeAction = this.handleLandscapeAction.bind(this);
  }

  handleLandscapeAction(row0, column0, direction, action) {
    let row1 = row0;
    let column1 = column0;
    if (direction == "north") {
      row1 = row0-1;
    } else if (direction == "west") {
      column1 = column0-1;
    } else if (direction == "east") {
      column1 = column0+1;
    } else {
      row1 = row0+1;
    }

    if (action == 'select') {
      this.props.onTilePlacement(this.props.player, this.props.placingTile.rank,
          row0, column0, row1, column1);
      return;
    }

    const square0 = this.refs['r'+row0 + 'c'+column0];
    const square1 = this.refs['r'+row1 + 'c'+column1];
    if (action == 'clear') {
      square0.setHintOverlay(null);
      square1.setHintOverlay(null);
    } else {
      square0.setHintOverlay(this.props.placingTile.squares[0]);
      square1.setHintOverlay(this.props.placingTile.squares[1]);
    }
  }

  render() {
    let label = this.props.player.name;
    if (this.props.placingTile) {
      label += ", should place the tile";
    }

    const dimension = this.props.squareDefs.length;
    let squares = [];
    for (let row = 0; row < dimension; row++) {
      for (let column = 0; column < dimension; column++) {
        let component;
        const square = this.props.squareDefs[row][column];
        if (square == null) {
          let placeable = {
            'north': false,
            'west': false,
            'east': false,
            'south': false
          };
          if (this.props.placingTile) {
            placeable.north = row > 0 && this.props.squareDefs[row-1][column] == null;
            placeable.west = column > 0 && this.props.squareDefs[row][column-1] == null;
            placeable.east = column+1 < dimension && this.props.squareDefs[row][column+1] == null;
            placeable.south = row+1 < dimension && this.props.squareDefs[row+1][column] == null;
          }

          component =
            <Square
              key={row + ',' + column}
              ref={'r' + row + 'c' + column}
              type="empty"
              row={row}
              column={column}
              placeable={placeable}
              onLandscapeAction={this.handleLandscapeAction}
            />;
        } else if (square.landscape != null) {
          component =
            <Square
              key={row + ',' + column}
              type="landscape"
              landscape={square.landscape}
              crowns={square.crowns}
            />;
        } else {
          component =
            <Square
              key={row + ',' + column}
              type="castle"
              color={this.props.player.colorName}
            />;
        }
        squares.push(component);
      }
    }

    // TODO: add shiftUp, shiftLeft, shiftDown, shiftRight buttons
    return (
      <div className="kingdom">
        <div>{label}</div>
        <div className="squares-container">
          {squares}
        </div>
      </div>
    );
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
        ownerContent = (
          <button
            onClick={() => this.props.onTileSelection(this.props.currentPlayer, this.props.rank)}>
              {this.props.currentPlayer.name}
          </button>
        );
      } else {
        ownerContent = <span>unclaimed</span>;
      }
    }
    else {
      ownerContent = <span>{this.props.tileOwner.name}</span>;
    }

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
    this.handleTilePlacement = this.handleTilePlacement.bind(this);
  }

  refreshGame(result)
  {
    this.setState({
      kingdoms: result.kingdoms,
      thisRoundTiles: result.thisRoundTiles,
      nextRoundTiles: result.nextRoundTiles,
      currentTurn: result.currentTurn,
      errorMessage: result.errorMessage
    });
  }

  componentDidMount() {
    fetch("getgame")
      .then(res => res.json())
      .then(
        (result) => {
          this.refreshGame(result);
        },
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
      (error) => {
        this.setState({
          error
        });
      }
    );
  }

  handleTilePlacement(currentPlayer, rank, row0, column0, row1, column1) {
    fetch("placetile/" + encodeURIComponent(currentPlayer.name) + "/" + rank +
        "/" + row0 + "/" + column0 + "/" + row1 + "/" + column1, {
      method: 'PUT'
    })
    .then(res => res.json())
    .then(
      (result) => {
        this.refreshGame(result);
      },
      (error) => {
        this.setState({
          error
        });
      }
    );
  }

  skipTilePlacement(currentPlayer, rank) {
    fetch("skiptile/" + encodeURIComponent(currentPlayer.name) + "/" + rank, {
      method: 'PUT'
    })
    .then(res => res.json())
    .then(
      (result) => {
        this.refreshGame(result);
      },
      (error) => {
        this.setState({
          error
        });
      }
    );
  }

  render() {
    const currentTurn = this.state.currentTurn;

    let errorMessage = "";
    if (this.state.errorMessage != null) {
      errorMessage = (
        <div>{this.state.errorMessage}</div>
      );
    }
    let skipButton = "";

    let status;
    let thisRoundSelector = null;
    let nextRoundSelector = null;
    let placingTile = null;
    if (currentTurn != null) {
      if (currentTurn.task == "GAME_OVER") {
        status = "Winner: " + currentTurn.player.name + ".";
      } else {
        status = "Next player: " + currentTurn.player.name + " to " + currentTurn.task + ".";
        if (currentTurn.task == "CHOOSING_INITIAL_TILE") {
          thisRoundSelector = currentTurn.player;
        } else if (currentTurn.task == "CHOOSING_NEXT_TILE") {
          nextRoundSelector = currentTurn.player;
        } else if (currentTurn.task == "PLACING_TILE") {
          if (currentTurn.skipReason != null) {
            status += " " + currentTurn.skipReason;
            skipButton = (
              <button onClick={() => this.skipTilePlacement(this.state.currentTurn.player, this.state.thisRoundTiles[0].rank)}>
                Skip Placement
              </button>
            );
          } else {
            placingTile = this.state.thisRoundTiles[0];
          }
        }
      }
    } else {
      status = "Initializing...";
    }

    const kingdoms = this.state.kingdoms.map((kingdom) =>
      <Kingdom
        key={kingdom.player.name}
        player={kingdom.player}
        squareDefs={kingdom.allSquares}
        placingTile={currentTurn != null && currentTurn.player.name == kingdom.player.name
            ? placingTile : null}
        onTilePlacement={this.handleTilePlacement}
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
        {errorMessage}
        <div>
          {kingdoms}
        </div>
        {thisRoundContent}
        {nextRoundContent}
        <div>
          {status}
          {skipButton}
        </div>
      </div>
    );
  }
}

ReactDOM.render(<Game />, document.getElementById("game"));