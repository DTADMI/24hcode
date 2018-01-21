import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class DetectionController extends Application {

	private final static int TIME_BETWEEN_ACQUING_TOUR_ON_GOING = 100;

	private final static String DROIT_FOLDER = "C:\\Users\\k008196\\Downloads\\testDroitStream";
	private final static String HAUT_FOLDER = "";

	private boolean onGoingTour = false;

	private ImageView imageViewDroit;
	private ImageView imageViewHaut;

	private Canvas canvasDroit;
	private Canvas canvasHaut;

	private DetectionImageDroit detectionDroit;
	private DetectionImageHaut detectionHaut;

	private GraphicsContext gcDroit;
	private GraphicsContext gcHaut;

	private DetectionMapper mapper;

	public static void main(String[] args) {
		launch(args);
	}

	Runnable getDroitRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					WatchService watcher = FileSystems.getDefault().newWatchService();
					detectionDroit.setRef(null);

					Path dir = Paths.get(DROIT_FOLDER);
					dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
					dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
					while (onGoingTour) {
						WatchKey key;
						try {
							key = watcher.take();
						} catch (InterruptedException ex) {
							return;
						}

						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();

							@SuppressWarnings("unchecked")
							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							final Path fileName = ev.context();

							System.out.println(kind.name() + ": " + fileName);

							if (kind == ENTRY_MODIFY && fileName.toString().endsWith("png")) {
								new Thread() {
									public void run() {
										try {
											RandomAccessFile raf = new RandomAccessFile(fileName.toFile(), "r");
											raf.getChannel().lock();


											BufferedImage image = ImageIO.read(fileName.toFile());
											if (detectionDroit.getRef() == null) {
												detectionDroit.setRef(image);
											} else {
												final List<DetectionLine> points2 = detectionDroit.getPoints(image);
												if (points2 != null)
													for (DetectionLine dl : points2) {
														mapper.addLineDectionDroit(dl);
													}
												Platform.runLater(new Runnable() {

													@Override
													public void run() {
														imageViewDroit.setImage(SwingFXUtils.toFXImage(image, null));
													}
												});
												if (points2 != null) {
													Platform.runLater(new Runnable() {

														@Override
														public void run() {
															for (DetectionLine points : points2) {
																Point2D previousPoint = null;
																for (Point2D p : points.getPoints()) {
																	if (previousPoint != null) {
																		gcDroit.strokeLine(previousPoint.getX(),
																				previousPoint.getY(), p.getX(),
																				p.getY());
																	}
																	previousPoint = p;
																}
															}
														}
													});

												}
											}
										} catch (IOException e) {
											e.printStackTrace();
										}
									}

								}.start();
							}

							boolean valid = key.reset();
							if (!valid) {
								break;
							}
						}
					}
				} catch (IOException x) {
					System.err.println(x);
				}

			}
		};
	}

	Runnable getHautRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					WatchService watcher = FileSystems.getDefault().newWatchService();
					detectionHaut.setRef(null);

					Path dir = Paths.get(HAUT_FOLDER);
					dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
					dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
					while (onGoingTour) {
						WatchKey key;
						try {
							key = watcher.take();
						} catch (InterruptedException ex) {
							return;
						}

						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();

							@SuppressWarnings("unchecked")
							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							Path fileName = ev.context();

							System.out.println(kind.name() + ": " + fileName);

							if (kind == ENTRY_MODIFY && fileName.toString().endsWith("png")) {
								try {
									BufferedImage image = ImageIO.read(fileName.toFile());
									if (detectionHaut.getRef() == null) {
										detectionHaut.setRef(image);
									} else {
										final List<DetectionLine> points2 = detectionHaut.getPoints(image);
										if (points2 != null)
											for (DetectionLine dl : points2) {
												mapper.addLineDectionHaut(dl);
											}
										Platform.runLater(new Runnable() {

											@Override
											public void run() {
												imageViewHaut.setImage(SwingFXUtils.toFXImage(image, null));
											}
										});
										if (points2 != null) {
											Platform.runLater(new Runnable() {

												@Override
												public void run() {
													for (DetectionLine points : points2) {
														Point2D previousPoint = null;
														for (Point2D p : points.getPoints()) {
															if (previousPoint != null) {
																gcHaut.strokeLine(previousPoint.getX(),
																		previousPoint.getY(), p.getX(), p.getY());
															}
															previousPoint = p;
														}
													}
												}
											});

										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}

						boolean valid = key.reset();
						if (!valid) {
							break;
						}
					}
				} catch (IOException x) {
					System.err.println(x);
				}

			}
		};
	}

	Runnable getDroitRunnable2() {
		return new Runnable() {

			@Override
			public void run() {
				for (File f : new File("C:\\Users\\k008196\\Downloads\\testimagesDroit").listFiles()) {
					try {
						BufferedImage image = ImageIO.read(f);
						if (detectionDroit.getRef() == null) {
							detectionDroit.setRef(image);
						} else {
							final List<DetectionLine> points2 = detectionDroit.getPoints(image);
							if (points2 != null)
								for (DetectionLine dl : points2) {
									mapper.addLineDectionDroit(dl);
								}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageViewDroit.setImage(SwingFXUtils.toFXImage(image, null));
								}
							});
							if (points2 != null) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										for (DetectionLine points : points2) {
											Point2D previousPoint = null;
											for (Point2D p : points.getPoints()) {
												if (previousPoint != null) {
													gcDroit.strokeLine(previousPoint.getX(), previousPoint.getY(),
															p.getX(), p.getY());
												}
												previousPoint = p;
											}
										}
									}
								});

							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

	}

	Runnable getHautRunnable2() {
		return new Runnable() {

			@Override
			public void run() {
				for (File f : new File("C:\\Users\\k008196\\Downloads\\testimagesHaut").listFiles()) {
					try {
						BufferedImage image = ImageIO.read(f);
						if (detectionHaut.getRef() == null) {
							detectionHaut.setRef(image);
						} else {
							final List<DetectionLine> points2 = detectionHaut.getPoints(image);
							if (points2 != null)
								for (DetectionLine dl : points2) {
									mapper.addLineDectionHaut(dl);
								}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageViewHaut.setImage(SwingFXUtils.toFXImage(image, null));
								}
							});
							if (points2 != null) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										for (DetectionLine points : points2) {
											Point2D previousPoint = null;
											for (Point2D p : points.getPoints()) {
												if (previousPoint != null) {
													gcHaut.strokeLine(previousPoint.getX(), previousPoint.getY(),
															p.getX(), p.getY());
												}
												previousPoint = p;
											}
										}
									}
								});

							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public void startTour() {
		onGoingTour = true;
		mapper.reinitializeScore();
		startDroitThread();
		startHautThread();
	}

	public void stopTour() {
		onGoingTour = false;
		sendScore(mapper.getCurrentScore(), mapper.getScores().size() > 0 ? mapper.getScores().get(0) : 0,
				mapper.getScores().size() > 1 ? mapper.getScores().get(1) : 0,
				mapper.getScores().size() > 2 ? mapper.getScores().get(2) : 0);
	}

	public String sendGetRequest(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
	}

	public void sendScore(int total, int tour1, int tour2, int tour3) {
		try {
			sendGetRequest(MessageFormat.format(
					"http://24hcode_git_flechettes/retournejson/newresultat/{0}.{1}.{2}.{3}", Integer.toString(total),
					Integer.toString(tour1), Integer.toString(tour2), Integer.toString(tour3)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isTourOnGoing() {
		// String response;
		// try {
		// response =
		// sendGetRequest("http://24hcode_git_flechettes/retournejson/statutlancer");
		// } catch (IOException e) {
		// e.printStackTrace();
		// return false;
		// }
		// return response.contains("\"statut\":\"1\"");
		return true;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setWidth(1000);
		primaryStage.setHeight(1000);

		ScrollPane scrollPane = new ScrollPane();
		VBox box = new VBox();
		scrollPane.setContent(box);

		Scene scene = new Scene(scrollPane);
		primaryStage.setScene(scene);
		primaryStage.show();

		detectionDroit = new DetectionImageDroit();
		StackPane stackPaneDroit = new StackPane();
		box.getChildren().add(stackPaneDroit);
		imageViewDroit = new ImageView();
		stackPaneDroit.getChildren().add(imageViewDroit);

		canvasDroit = new Canvas(detectionDroit.imageWidth, detectionDroit.imageHeight);
		gcDroit = canvasDroit.getGraphicsContext2D();
		gcDroit.setStroke(Color.HOTPINK);
		stackPaneDroit.getChildren().add(canvasDroit);

		detectionHaut = new DetectionImageHaut();
		StackPane stackPaneHaut = new StackPane();
		box.getChildren().add(stackPaneHaut);
		imageViewHaut = new ImageView();
		stackPaneHaut.getChildren().add(imageViewHaut);

		canvasHaut = new Canvas(detectionHaut.imageWidth, detectionHaut.imageHeight);
		gcHaut = canvasHaut.getGraphicsContext2D();
		gcHaut.setStroke(Color.HOTPINK);
		stackPaneHaut.getChildren().add(canvasHaut);

		mapper = new DetectionMapper(detectionDroit, detectionHaut);

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				boolean newOngoing = isTourOnGoing();
				if (newOngoing != onGoingTour) {
					if (newOngoing) {
						startTour();
					} else {
						stopTour();
					}
				}
			}
		};
		new Timer().schedule(timerTask, 0, TIME_BETWEEN_ACQUING_TOUR_ON_GOING);

	}

	private void startHautThread() {
		Thread threadHaut = new Thread(getHautRunnable());
		threadHaut.start();
	}

	private void startDroitThread() {
		Thread threadDroit = new Thread(getDroitRunnable());
		threadDroit.start();
	}
}
