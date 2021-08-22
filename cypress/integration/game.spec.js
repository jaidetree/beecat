before(() => {
  cy.clearLocalStorage();
  cy.intercept("/data/game.json", { fixture: "game.json" });
  cy.visit("http://localhost:8000");
});

it("Game initializes", () => {
  cy.get("h1").should("contain", "Beecat");
  cy.wait(250);
  cy.get(".honeycombs").should((el) => {
    const text = el.text();

    expect(Array.from(text).sort()).to.deep.equal([
      "a",
      "f",
      "g",
      "i",
      "r",
      "t",
      "y",
    ]);
  });
});

it("Starts at beginner rank", () => {
  cy.contains("Beginner");
});

it("Enter a character", () => {
  cy.get("body").type("arty");
  cy.get(".letters").should("have.text", "arty");
  cy.get("body").type("{enter}");
  cy.contains("Nice");
  cy.get(".points.active").should("have.text", "+ 1");
  cy.wait(500);
  cy.get(".points.active").should("not.exist");
});

it("Can enter a pangram", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("gratify{enter}");
  cy.contains("Ooh a pangram");
  cy.get(".points.active").should("have.text", "+ 14");
  cy.wait(500);
  cy.get(".points.active").should("not.exist");
});

it("Moves to Good Start rank", () => {
  cy.contains("Good Start");
});

it("Receives 8 points for 8 length word", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("graffiti{enter}");
  cy.contains("Super");
  cy.get(".points.active").should("have.text", "+ 8");
  cy.wait(500);
  cy.get(".points.active").should("not.exist");
});

it("Rejects if word already found", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("graffiti{enter}");
  cy.contains("You already found this word remember?");
  cy.get(".points.active").should("not.exist");
  cy.wait(500);
  cy.get(".letters").should("have.text", "");
});

it("Moves to Moving Up rank", () => {
  cy.contains("Moving Up");
  cy.get(".cursor").invoke("attr", "style").should("equal", "width: 7.31707%;");
  cy.get(".milestone.active").should("have.length", 3);
});

it("Show a message when ", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("{enter}");
  cy.contains("No");
  cy.wait(1500);
  cy.get(".letters").should("have.text", "");
});

it("Click letters and submit", () => {
  cy.get(".letters").should("have.text", "");
  cy.get(".honeycombs svg").eq(1).click();
  cy.get(".honeycombs svg").eq(0).click();
  cy.get(".honeycombs svg").eq(4).click();
  cy.get(".honeycombs svg").eq(6).click();
  cy.get(".letters").should("have.text", "fart");
  cy.get(".actions button").eq(2).click();
  cy.contains("Nice");
  cy.contains("+ 1");
  cy.wait(500);
  cy.get(".points.active").should("not.exist");
});

it("Deletes a character on backspace", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("a");
  cy.get(".letters").should("have.text", "a");
  cy.get("body").type("{backspace}");
  cy.get(".letters").should("have.text", "");
});

it("Deletes a character when clicking delete", () => {
  cy.get(".letters").should("have.text", "");
  cy.get("body").type("a");
  cy.get(".letters").should("have.text", "a");
  cy.get(".actions button").eq(0).click();
  cy.get(".letters").should("have.text", "");
});

it("Shuffles characters when shuffle is clicked", () => {
  cy.get(".honeycombs").then((el) => {
    const original = Array.from(el.text());

    cy.get(".actions button").eq(1).click();

    cy.get(".honeycombs").should((el) => {
      const text = el.text();

      expect(Array.from(text)).to.not.deep.equal(original);
    });
  });
});

it("Shows rankings when rank is clicked", () => {
  cy.contains("Good ( 24 )").click();
  cy.get(".modal li").eq(1).should("have.text", "Beginner0");
  cy.contains("Ultimate Genius");
  cy.get(".modal li").last().should("have.text", "Ultimate Genius41");
});

it("Can close the modal", () => {
  cy.wait(500);
  cy.get(".modal button").click();
  cy.wait(500);
  cy.get(".modal").should("not.exist");
  cy.contains("Good ( 24 )").click();
  cy.contains("Rankings");
  cy.get(".modal").click();
  cy.wait(500);
  cy.get("body").type("{esc}");
  cy.wait(500);
  cy.get(".modal").should("not.exist");
});

it("Sees the completion modal", () => {
  cy.get(".honeycombs");
  cy.fixture("game.json").as("gameData");
  cy.get("@gameData")
    .its("answers")
    .then((answers) => {
      const used = new Set(["arty", "fart", "graffiti", "gratify"]);
      const filtered = answers.filter((answer) => !used.has(answer));
      return filtered.slice(0, 13);
    })
    .as("filtered");

  cy.get(".modal").should("not.exist");

  cy.get("@filtered")
    .each((answer, index, list) => {
      cy.get("body")
        .type(`${answer}{enter}`)
        .then(() => cy.wait(2000))
        .then(() => cy.get(".list").contains(answer));
    })
    .then(() => {
      cy.contains("You Did It!");
    });
});
