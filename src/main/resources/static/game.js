const firebaseConfig = {
    apiKey: "AIzaSyCGrAxRTm8GhiodxC4FtniZVeCIYpZZ7Bg",
    authDomain: "game1024-b1c05.firebaseapp.com",
    databaseURL: "https://game1024-b1c05-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "game1024-b1c05",
    storageBucket: "game1024-b1c05.appspot.com",
    messagingSenderId: "72717204728",
    appId: "1:72717204728:web:684994dfc70e2533ee4f0d"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.database();

// Authentication
const loginBtn = document.getElementById('loginBtn');
const logoutBtn = document.getElementById('logoutBtn');
const userInfo = document.getElementById('userInfo');
const saveGameBtn = document.getElementById('saveGameBtn');
const newGameBtn = document.getElementById('newGameBtn');
const loadGameBtn = document.getElementById('loadGameBtn');


loginBtn.onclick = () => {
    const provider = new firebase.auth.GoogleAuthProvider();
    auth.signInWithPopup(provider);
};

logoutBtn.onclick = () => {
    auth.signOut();
};

auth.onAuthStateChanged(user => {
    if (user) {
        loginBtn.style.display = 'none';
        logoutBtn.style.display = 'inline-block';
        userInfo.innerHTML = `<p>Welcome, ${user.displayName}!</p>`;
        saveGameBtn.disabled = false;
        loadGameBtn.disabled = false;
        loadGame(user.uid);
        loadComments();
        loadRatings();
        showLeaderboard();
        showAllComments();
        showNotification(`Welcome back, ${user.displayName}!`);
    } else {
        loginBtn.style.display = 'inline-block';
        logoutBtn.style.display = 'none';
        userInfo.innerHTML = '';
        saveGameBtn.disabled = true;
        loadGameBtn.disabled = true;
        initBoard();
    }
});

// Comments functionality
const commentInput = document.getElementById('commentInput');
const sendComment = document.getElementById('sendComment');
const commentsList = document.getElementById('commentsList');

sendComment.onclick = () => {
    const user = auth.currentUser;
    const comment = commentInput.value.trim();
    if (!user) {
        showNotification('Please sign in to comment');
        return;
    }

    if (!comment) {
        showNotification('Comment cannot be empty');
        return;
    }

    const isLiked = /(?:\byes\b|\blike\b)/i.test(comment);

    const commentData = {
        user: user.displayName,
        uid: user.uid,
        text: comment,
        timestamp: Date.now(),
        isLiked: isLiked,
    };


    db.ref('comments').push(commentData)
        .then(() => {
            commentInput.value = '';
            showNotification('Comment posted!');
            loadComments();
            showAllComments();
        });
};

function loadComments() {
    commentsList.innerHTML = '';
    db.ref('comments').orderByChild('timestamp').limitToLast(5).once('value', snapshot => {
        const comments = snapshot.val();
        if (comments) {
            Object.values(comments).reverse().forEach(comment => {
                const div = document.createElement('div');
                div.className = 'comment-item';
                div.innerHTML = `
                    <div class="comment-header">
                        <strong>${comment.user}</strong>
                        <span class="comment-date">${new Date(comment.timestamp).toLocaleString()}</span>
                    </div>
                    <p>${comment.text.replace(/\b(yes|like)\b/gi, '❤️')}</p>
                `;
                commentsList.appendChild(div);
            });
        }
    });
}

// Rating functionality
document.getElementById('rateGame').onclick = () => {
    const user = auth.currentUser;
    const rating = parseInt(document.getElementById('ratingInput').value);
    if (!user) {
        showNotification('Please sign in to rate the game');
        return;
    }

    db.ref('ratings/' + user.uid).set(rating).then(() => {
        showNotification('Rating submitted!');
        loadRatings();
    });
};

function loadRatings() {
    const ratingRef = firebase.database().ref('ratings');

    ratingRef.once('value').then(snapshot => {
        const ratings = snapshot.val();
        if (!ratings) {
            displayRating(0);
            return;
        }

        const values = Object.values(ratings);
        const sum = values.reduce((acc, val) => acc + val, 0);
        const avg = sum / values.length;

        displayRating(avg);
    }).catch(error => {
        console.error("Error loading ratings:", error);
        document.getElementById('rating-value').textContent = 'No data';
    });
}

function displayRating(averageRating = 3) {
    const starsContainer = document.getElementById('rating-stars');
    const ratingValue = document.getElementById('rating-value');
    starsContainer.innerHTML = '';

    const filledStars = Math.floor(averageRating);
    const hasHalfStar = averageRating - filledStars >= 0.5;
    const totalStars = 5;

    for (let i = 0; i < totalStars; i++) {
        const star = document.createElement('span');
        star.classList.add('star');
        if (i < filledStars || (i === filledStars && hasHalfStar)) {
            star.classList.add('filled');
        }
        star.innerHTML = '&#9733;';
        starsContainer.appendChild(star);
    }

    ratingValue.textContent = averageRating.toFixed(1);
}

// Game functionality
const boardSize = 4;
let board = [];
let score = 0;
let bestScore = 0;
let savedGame = null;
let previousBoard = null;
let previousScore = 0;

// Button event listeners for game actions
saveGameBtn.onclick = () => {
    saveGameSnapshot();
};

newGameBtn.onclick = () => {
    if (confirm('Are you sure you want to start a new game? Current progress will be lost if not saved.')) {
        initBoard();
        showNotification('New game started!');
    }
};

loadGameBtn.onclick = () => {
    loadGameSnapshot();
};

function startTimer() {
    startTime = Date.now();
    if (timerInterval) clearInterval(timerInterval);
    timerInterval = setInterval(() => {
        const elapsed = Math.floor((Date.now() - startTime) / 1000);
        document.getElementById('timer').textContent = formatTime(elapsed);
    }, 1000);
}

function stopTimer() {
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }
}

function formatTime(seconds) {
    const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
    const secs = (seconds % 60).toString().padStart(2, '0');
    return `${mins}:${secs}`;
}

function saveGameSnapshot() {
    const user = auth.currentUser;
    if (!user) {
        showNotification('Please sign in to save your game');
        return;
    }

    const gameSnapshot = {
        board: board,
        score: score,
        timestamp: Date.now()
    };

    db.ref('savedGames/' + user.uid).set(gameSnapshot)
        .then(() => {
            savedGame = gameSnapshot;
            showNotification('Game saved successfully!');
        })
        .catch(error => {
            showNotification('Error saving game: ' + error.message);
        });
}

function loadGameSnapshot() {
    const user = auth.currentUser;
    if (!user) {
        showNotification('Please sign in to load your game');
        return;
    }

    db.ref('savedGames/' + user.uid).once('value').then(snapshot => {
        const data = snapshot.val();
        if (data) {
            board = data.board;
            score = data.score;
            updateScoreDisplay();
            updateBoard();
            showNotification('Game loaded successfully!');
        } else {
            showNotification('No saved game found');
        }
    }).catch(error => {
        showNotification('Error loading game: ' + error.message);
    });
}

function showNotification(message) {
    let container = document.querySelector('.notification-container');

    if (!container) {
        container = document.createElement('div');
        container.className = 'notification-container';
        document.body.appendChild(container);
    }

    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;

    container.appendChild(notification);

    // Trigger animation
    requestAnimationFrame(() => {
        notification.classList.add('show');
    });

    setTimeout(() => {
        notification.classList.remove('show');
        notification.classList.add('hide');
        setTimeout(() => {
            notification.remove();
        }, 500);
    }, 4000);
}

function toggleSection(sectionId) {
    const sections = ['leaderboardSection', 'commentsSection', 'rulesSection'];
    sections.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            if (id === sectionId) {
                const isCurrentlyVisible = el.style.display === 'block';
                el.style.display = isCurrentlyVisible ? 'none' : 'block';

                // If opening the section
                if (!isCurrentlyVisible) {
                    if (sectionId === 'leaderboardSection') {
                        showLeaderboard();
                    } else if (sectionId === 'commentsSection') {
                        showAllComments();
                    }
                }
            } else {
                el.style.display = 'none';
            }
        }
    });
}

function initBoard() {
    board = Array(boardSize).fill().map(() => Array(boardSize).fill(0));
    score = 0;
    updateScoreDisplay();
    addTile();
    addTile();
    updateBoard();
}

function saveGame() {
    const user = auth.currentUser;
    if (!user) return;

    // Save current game state
    const gameData = {
        board: board,
        score: score,
        timestamp: Date.now()
    };

    db.ref("games/" + user.uid).set(gameData)
        .catch((error) => showNotification("Failed to save game state: " + error.message));

    // Update leaderboard if current score is a personal best
    if (score > bestScore && score > 0) {
        bestScore = score;
        document.getElementById('bestScore').textContent = bestScore;

        // Save to leaderboard with proper data structure
        db.ref('leaderboard/' + user.uid).set({
            name: user.displayName || 'Anonymous',
            score: bestScore,
            timestamp: Date.now()
        }).then(() => {
            console.log("Leaderboard updated successfully");
        }).catch(error => {
            console.error("Error updating leaderboard:", error);
        });
    }
}

function loadGame(uid) {
    db.ref('games/' + uid).once('value').then(snapshot => {
        const data = snapshot.val();
        if (data && data.board && data.score !== undefined) {
            board = data.board;
            score = data.score;
            updateScoreDisplay();
            updateBoard();
        } else {
            initBoard();
        }
    }).catch(error => {
        console.error("Error loading game:", error);
        initBoard();
    });

    db.ref('leaderboard/' + uid).once('value').then(snapshot => {
        const data = snapshot.val();
        if (data && data.score) {
            bestScore = data.score;
            document.getElementById('bestScore').textContent = bestScore;
        } else {
            bestScore = 0;
            document.getElementById('bestScore').textContent = "0";
        }
    }).catch(error => {
        console.error("Error loading best score:", error);
    });

    // Check if there's a manually saved game
    db.ref('savedGames/' + uid).once('value').then(snapshot => {
        if (snapshot.exists()) {
            savedGame = snapshot.val();
            loadGameBtn.disabled = false;
        } else {
            savedGame = null;
            loadGameBtn.disabled = true;
        }
    }).catch(error => {
        console.error("Error checking saved games:", error);
    });
}

function addTile() {
    const empty = [];
    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize; j++) {
            if (board[i][j] === 0) empty.push([i, j]);
        }
    }
    if (empty.length === 0) return;

    const [x, y] = empty[Math.floor(Math.random() * empty.length)];
    board[x][y] = Math.random() < 0.9 ? 2 : 4;

    // Add animation class to the new tile
    setTimeout(() => {
        const tiles = document.querySelectorAll('.tile');
        const index = x * boardSize + y;
        if (tiles[index]) {
            tiles[index].classList.add('animate');
        }
    }, 50);
}

function updateScoreDisplay() {
    console.log('Updating score display:', score);
    document.getElementById('score').textContent = score;

    // Update best score if current score is higher
    if (score > bestScore) {
        bestScore = score;
        document.getElementById('bestScore').textContent = bestScore;
    }
}

function updateBoard() {
    const boardDiv = document.getElementById('gameBoard');
    boardDiv.innerHTML = '';

    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize; j++) {
            const tile = document.createElement('div');
            tile.className = 'tile';
            tile.dataset.value = board[i][j];
            tile.textContent = board[i][j] === 0 ? '' : board[i][j];
            boardDiv.appendChild(tile);
        }
    }

    updateScoreDisplay();

    if (auth.currentUser) {
        saveGame();
    }

    if (isGameOver()) {
        setTimeout(() => {
            showNotification(`Game Over! Your score: ${score}`);

            if (auth.currentUser && score > 0) {
                db.ref('leaderboard/' + auth.currentUser.uid).set({
                    name: auth.currentUser.displayName || 'Anonymous',
                    score: score,
                    timestamp: Date.now()
                });
            }

            setTimeout(() => {
                initBoard();
            }, 2000);
        }, 300);
    }
}

function isGameOver() {
    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize; j++) {
            if (board[i][j] === 0) return false;
        }
    }

    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize - 1; j++) {
            if (board[i][j] === board[i][j + 1]) return false;
        }
    }

    for (let j = 0; j < boardSize; j++) {
        for (let i = 0; i < boardSize - 1; i++) {
            if (board[i][j] === board[i + 1][j]) return false;
        }
    }

    return true;
}

function move(dir) {
    const oldBoard = JSON.stringify(board);
    const tempBoard = JSON.parse(JSON.stringify(board));
    const tempScore = score;
    const rotated = rotate(dir);
    for (let row of rotated) {
        const newRow = slide(row);
        row.splice(0, row.length, ...newRow);
    }
    board = unrotate(rotated, dir);

    if (oldBoard !== JSON.stringify(board)) {
        previousBoard = tempBoard;
        previousScore = tempScore;

        console.log('Saving previous state:', previousBoard, previousScore);

        addTile();
        updateBoard();
    }
}

function slide(row) {
    row = row.filter(v => v);
    for (let i = 0; i < row.length - 1; i++) {
        if (row[i] === row[i + 1]) {
            row[i] *= 2;
            score += row[i];
            row[i + 1] = 0;
        }
    }
    return row.filter(v => v).concat(Array(boardSize - row.filter(v => v).length).fill(0));
}

function rotate(dir) {
    let newBoard = JSON.parse(JSON.stringify(board));
    if (dir === 'left') return newBoard;
    if (dir === 'right') return newBoard.map(row => row.reverse());
    if (dir === 'up') return transpose(newBoard);
    if (dir === 'down') return transpose(newBoard).map(row => row.reverse());
}

function unrotate(rotated, dir) {
    if (dir === 'left') return rotated;
    if (dir === 'right') return rotated.map(row => row.reverse());
    if (dir === 'up') return transpose(rotated);
    if (dir === 'down') return transpose(rotated.map(row => row.reverse()));
}

function transpose(m) {
    return m[0].map((_, i) => m.map(row => row[i]));
}


function showLeaderboard() {
    const list = document.getElementById('leaderboardList');
    list.innerHTML = '<div class="loading">Loading leaderboard...</div>';

    db.ref('leaderboard').once('value')
        .then(snapshot => {
            const scores = [];
            snapshot.forEach(child => {
                const data = child.val();
                if (data && data.score > 0) {
                    scores.push({
                        uid: child.key,
                        name: data.name || 'Anonymous',
                        score: data.score,
                        timestamp: data.timestamp || 0
                    });
                }
            });

            scores.sort((a, b) => b.score - a.score);

            const topScores = scores.slice(0, 20);

            list.innerHTML = '';

            if (topScores.length === 0) {
                list.innerHTML = '<div class="no-data">No scores yet. Be the first to play!</div>';
                return;
            }



            // Add each score item
            topScores.forEach((s, index) => {
                const date = new Date(s.timestamp);
                const formattedDate = date.toLocaleDateString();

                const item = document.createElement('div');
                item.className = 'leaderboard-item';

                if (auth.currentUser && s.uid === auth.currentUser.uid) {
                    item.classList.add('current-user');
                }

                item.innerHTML = `
                    <span class="rank">${index + 1}</span>
                    <span class="name">${s.name}</span>
                    <span class="score">${s.score}</span>
                    <span class="date">${formattedDate}</span>
                `;
                list.appendChild(item);
            });
        })
        .catch(error => {
            list.innerHTML = `<div class="error">Error loading leaderboard: ${error.message}</div>`;
            console.error("Leaderboard error:", error);
        });
}



function initThemeToggle() {
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');

    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;

    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
        document.body.classList.add('dark-mode');
        themeIcon.textContent = '☀️';
    }

    themeToggleBtn.addEventListener('click', () => {
        document.body.classList.toggle('dark-mode');

        if (document.body.classList.contains('dark-mode')) {
            localStorage.setItem('theme', 'dark');
            themeIcon.textContent = '☀️';
        } else {
            localStorage.setItem('theme', 'light');
            themeIcon.textContent = '🌙';
        }
    });
}

function initTouchControls() {
    const gameBoard = document.getElementById('gameBoard');
    let startX, startY, endX, endY;
    const minSwipeDistance = 50;

    gameBoard.addEventListener('touchstart', (e) => {
        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
        e.preventDefault();
    }, { passive: false });

    gameBoard.addEventListener('touchmove', (e) => {
        e.preventDefault();
    }, { passive: false });

    gameBoard.addEventListener('touchend', (e) => {
        endX = e.changedTouches[0].clientX;
        endY = e.changedTouches[0].clientY;

        const diffX = endX - startX;
        const diffY = endY - startY;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Horizontal swipe
            if (Math.abs(diffX) > minSwipeDistance) {
                if (diffX > 0) {
                    move('right');
                } else {
                    move('left');
                }
            }
        } else {
            // Vertical swipe
            if (Math.abs(diffY) > minSwipeDistance) {
                if (diffY > 0) {
                    move('down');
                } else {
                    move('up');
                }
            }
        }
    });
}

function showAllComments() {
    const allCommentsList = document.getElementById('allCommentsList');
    allCommentsList.innerHTML = '<div class="loading">Loading comments...</div>';

    db.ref('comments').orderByChild('timestamp').once('value', snapshot => {
        const comments = snapshot.val();
        allCommentsList.innerHTML = '';


        if (comments) {
            Object.values(comments).reverse().forEach(comment => {
                const div = document.createElement('div');
                div.className = 'comment-item';
                div.innerHTML = `
                    <div class="comment-header">
                        <strong>${comment.user}</strong>
                        <span class="comment-date">${new Date(comment.timestamp).toLocaleString()}</span>
                    </div>
                    <p>${comment.text.replace(/\b(yes|like)\b/gi, '❤️')}</p>
                `;
                allCommentsList.appendChild(div);
            });
        }
        else {
            allCommentsList.innerHTML = '<div class="no-data">No comments yet</div>';
        }
    }).catch(error => {
        allCommentsList.innerHTML = `<div class="error">Error loading comments: ${error.message}</div>`;
        console.error("Error loading comments:", error);
    });
}

document.addEventListener('keydown', e => {
    switch (e.key.toLowerCase()) {
        case "a":
        case "arrowleft":
            move('left');
            break;
        case "d":
        case "arrowright":
            move('right');
            break;
        case "w":
        case "arrowup":
            move('up');
            break;
        case "s":
        case "arrowdown":
            move('down');
            break;
    }
});


document.addEventListener('DOMContentLoaded', function() {
    initBoard();
    displayRating();
    initThemeToggle();
    initTouchControls();
});


document.getElementById('replaceRow').addEventListener('click', () => {
    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize; j++) {
            if ((board[0][j] || board[3][j])!= 0) {
                board[0][j] = 0;
                board[3][j] = 0;
            }
        }
    }
    updateBoard();
    showNotification('Deleted 1 row and 4 row');
});



